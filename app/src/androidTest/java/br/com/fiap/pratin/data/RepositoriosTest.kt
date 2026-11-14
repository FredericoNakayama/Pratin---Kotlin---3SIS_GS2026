package br.com.fiap.pratin.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.fiap.pratin.data.local.db.PratinDatabase
import br.com.fiap.pratin.data.repository.RoomAlimentoRepository
import br.com.fiap.pratin.data.repository.RoomReservaRepository
import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.data.seed.SeedInitializer
import br.com.fiap.pratin.domain.error.ErroRegra
import br.com.fiap.pratin.domain.error.RegraNegocioException
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.model.Unidade
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/** Transações dos repositórios em um banco Room em memória (fase F3). */
@RunWith(AndroidJUnit4::class)
class RepositoriosTest {

    private val zona = ZoneId.of("America/Sao_Paulo")
    private val agora = LocalDateTime.of(2026, 11, 14, 10, 0)
    private val clock = Clock.fixed(agora.atZone(zona).toInstant(), zona)

    private lateinit var db: PratinDatabase
    private lateinit var alimentos: RoomAlimentoRepository
    private lateinit var reservas: RoomReservaRepository

    @Before
    fun criarBanco() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, PratinDatabase::class.java).build()
        alimentos = RoomAlimentoRepository(db, clock)
        reservas = RoomReservaRepository(db, clock, gerarCodigo = { "ABC234" })
    }

    @After
    fun fecharBanco() = db.close()

    private fun esperarErro(esperado: ErroRegra, bloco: suspend () -> Unit) = runBlocking {
        try {
            bloco()
            fail("Esperava $esperado")
        } catch (e: RegraNegocioException) {
            assertEquals(esperado, e.erro)
        }
    }

    private suspend fun publicarPaoDeForma(): Long = alimentos.publicar(
        NovoAnuncio(
            nome = "Pão de forma",
            categoria = Categoria.PAES,
            quantidadeTexto = "12",
            unidade = Unidade.PACOTE,
            dataValidade = agora.toLocalDate().plusDays(1),
            retiradaInicio = agora.plusHours(1),
            retiradaFim = agora.plusHours(3),
            endereco = SeedData.padaria.endereco,
            declaracaoProprio = true,
        ),
        SeedData.padaria,
    )

    @Test
    fun publicarGravaDisponivelComDonoDoPerfil() = runBlocking {
        val id = publicarPaoDeForma()
        val alimento = alimentos.observarPorId(id).first()!!
        assertEquals(StatusAlimento.DISPONIVEL, alimento.status)
        assertEquals(SeedData.padaria.cnpj, alimento.estabelecimentoCnpj)
        assertEquals(listOf(id), alimentos.observarDisponiveis().first().map { it.id })
    }

    @Test
    fun publicarInvalidoNaoGravaNada() {
        esperarErro(ErroRegra.ANUNCIO_INVALIDO) { alimentos.publicar(NovoAnuncio(), SeedData.padaria) }
        runBlocking { assertTrue(alimentos.observarDisponiveis().first().isEmpty()) }
    }

    @Test
    fun ongNaoPublica() {
        esperarErro(ErroRegra.APENAS_ESTABELECIMENTOS) { alimentos.publicar(NovoAnuncio(), SeedData.ongMesaCheia) }
    }

    @Test
    fun reservarMudaAlimentoECriaReservaAtiva() = runBlocking {
        val id = publicarPaoDeForma()
        val reservaId = reservas.reservar(id, SeedData.ongMesaCheia)

        val detalhe = reservas.observarPorId(reservaId).first()!!
        assertEquals(StatusReserva.ATIVA, detalhe.reserva.status)
        assertEquals("ABC234", detalhe.reserva.codigoRetirada)
        assertEquals(StatusAlimento.RESERVADO, detalhe.alimento.status)
        assertTrue(alimentos.observarDisponiveis().first().isEmpty())
    }

    @Test
    fun segundaReservaFalhaSemAlterarNada() {
        val id = runBlocking { publicarPaoDeForma().also { reservas.reservar(it, SeedData.ongMesaCheia) } }
        esperarErro(ErroRegra.JA_RESERVADO) { reservas.reservar(id, SeedData.ongMesaCheia) }
        runBlocking { assertEquals(1, reservas.observarDaOng(SeedData.ongMesaCheia.cnpj).first().size) }
    }

    @Test
    fun estabelecimentoNaoReserva() {
        val id = runBlocking { publicarPaoDeForma() }
        esperarErro(ErroRegra.APENAS_ONGS) { reservas.reservar(id, SeedData.padaria) }
    }

    @Test
    fun cancelarReservaDevolveParaDisponivel() = runBlocking {
        val id = publicarPaoDeForma()
        val reservaId = reservas.reservar(id, SeedData.ongMesaCheia)
        reservas.cancelar(reservaId, SeedData.ongMesaCheia)

        assertEquals(StatusReserva.CANCELADA, reservas.observarPorId(reservaId).first()!!.reserva.status)
        assertEquals(StatusAlimento.DISPONIVEL, alimentos.observarPorId(id).first()!!.status)
    }

    @Test
    fun codigoErradoNaoAlteraNada() {
        val id = runBlocking { publicarPaoDeForma().also { reservas.reservar(it, SeedData.ongMesaCheia) } }
        esperarErro(ErroRegra.CODIGO_INVALIDO) { reservas.confirmarRetirada(id, "ZZZ999", SeedData.padaria) }
        runBlocking { assertEquals(StatusAlimento.RESERVADO, alimentos.observarPorId(id).first()!!.status) }
    }

    @Test
    fun codigoCertoConcluiRetirada() = runBlocking {
        val id = publicarPaoDeForma()
        val reservaId = reservas.reservar(id, SeedData.ongMesaCheia)
        reservas.confirmarRetirada(id, " abc234 ", SeedData.padaria)

        assertEquals(StatusAlimento.RETIRADO, alimentos.observarPorId(id).first()!!.status)
        assertEquals(StatusReserva.CONCLUIDA, reservas.observarPorId(reservaId).first()!!.reserva.status)
    }

    @Test
    fun outroEstabelecimentoNaoConfirmaRetirada() {
        val id = runBlocking { publicarPaoDeForma().also { reservas.reservar(it, SeedData.ongMesaCheia) } }
        esperarErro(ErroRegra.NAO_E_DONO) { reservas.confirmarRetirada(id, "ABC234", SeedData.mercado) }
    }

    @Test
    fun cancelarAnuncioSoQuandoDisponivel() = runBlocking {
        val id = publicarPaoDeForma()
        alimentos.cancelarAnuncio(id, SeedData.padaria)
        assertEquals(StatusAlimento.CANCELADO, alimentos.observarPorId(id).first()!!.status)
        esperarErro(ErroRegra.NAO_PODE_CANCELAR_ANUNCIO) { alimentos.cancelarAnuncio(id, SeedData.padaria) }
    }

    @Test
    fun expiracaoAtingeAlimentoEReservaAtiva() = runBlocking {
        val id = publicarPaoDeForma()
        val reservaId = reservas.reservar(id, SeedData.ongMesaCheia)

        // Avança o relógio para depois do fim da janela (agora + 3 h).
        val depois = Clock.fixed(Instant.now(clock).plusSeconds(4 * 3600), zona)
        RoomAlimentoRepository(db, depois).expirarVencidos()

        assertEquals(StatusAlimento.EXPIRADO, alimentos.observarPorId(id).first()!!.status)
        assertEquals(StatusReserva.EXPIRADA, reservas.observarPorId(reservaId).first()!!.reserva.status)
    }

    @Test
    fun favoritoAlternaEPersiste() = runBlocking {
        val id = publicarPaoDeForma()
        alimentos.alternarFavorito(id)
        assertTrue(alimentos.observarPorId(id).first()!!.favorito)
        alimentos.alternarFavorito(id)
        assertFalse(alimentos.observarPorId(id).first()!!.favorito)
    }

    @Test
    fun seedRodaUmaVezSo() = runBlocking {
        val seed = SeedInitializer(db, clock)
        assertTrue(seed.executarSeNecessario())
        assertFalse(seed.executarSeNecessario())

        val disponiveis = alimentos.observarDisponiveis().first()
        assertEquals(13, disponiveis.size)
        val reservasDaOng = reservas.observarDaOng(SeedData.ongMesaCheia.cnpj).first()
        assertEquals(listOf(StatusReserva.ATIVA, StatusReserva.CONCLUIDA), reservasDaOng.map { it.reserva.status })
        assertEquals(SeedData.CODIGO_DEMO, reservasDaOng.first().reserva.codigoRetirada)
        assertNull(alimentos.observarAnuncios(SeedData.restaurante.cnpj).first().firstOrNull { it.reservaAtiva == null })
    }
}
