package br.com.fiap.pratin.data

import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.data.mapper.paraDominio
import br.com.fiap.pratin.data.mapper.paraEntidade
import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.rules.AnuncioValidator
import br.com.fiap.pratin.domain.rules.CnpjValidator
import br.com.fiap.pratin.domain.rules.CodigoGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

/** RN16 — os dados de exemplo precisam obedecer às mesmas regras de um anúncio real. */
class SeedDataTest {

    /** Testa vários horários do dia, inclusive tarde da noite (demonstração gravada à noite). */
    private val agoras = listOf(0, 8, 13, 20, 23).map { LocalDateTime.of(2026, 11, 14, it, 30) }

    private fun AlimentoEntity.comoFormulario() = NovoAnuncio(
        nome = nome,
        categoria = categoria,
        quantidadeTexto = quantidade.toString(),
        unidade = unidade,
        dataValidade = dataValidade,
        retiradaInicio = retiradaInicio,
        retiradaFim = retiradaFim,
        endereco = endereco,
        fotoUrl = fotoUrl.orEmpty(),
        declaracaoProprio = declaracaoProprio,
    )

    @Test
    fun `anúncios disponíveis passam na validação do formulário em qualquer horário`() {
        agoras.forEach { agora ->
            SeedData.alimentosDisponiveis(agora).forEach {
                val erros = AnuncioValidator.validar(it.comoFormulario(), agora)
                assertTrue("${it.nome} às ${agora.hour}h: $erros", erros.isEmpty())
            }
        }
    }

    @Test
    fun `marmita reservada continua com a janela aberta no dia do seed`() {
        agoras.forEach { agora ->
            assertTrue(SeedData.marmitaReservada(agora).paraDominio().janelaAberta(agora))
        }
    }

    @Test
    fun `todos os CNPJs de demonstração são válidos`() {
        SeedData.perfisDemonstracao.forEach { assertTrue(it.nome, CnpjValidator.isValido(it.cnpj)) }
    }

    @Test
    fun `códigos de demonstração seguem o alfabeto`() {
        val codigo = SeedData.reservaDaMarmita(1, agoras.first()).codigoRetirada
        assertEquals(CodigoGenerator.TAMANHO, codigo.length)
        assertTrue(codigo.all { it in CodigoGenerator.ALFABETO })
    }

    @Test
    fun `pão francês é histórico retirado`() {
        assertEquals(StatusAlimento.RETIRADO, SeedData.paoFrancesRetirado(agoras.first()).status)
    }

    @Test
    fun `mapper ida e volta preserva o alimento`() {
        val entidade = SeedData.alimentosDisponiveis(agoras.first()).first().copy(id = 7)
        assertEquals(entidade, entidade.paraDominio().paraEntidade())
    }
}
