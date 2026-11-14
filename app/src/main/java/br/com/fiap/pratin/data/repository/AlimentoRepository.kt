package br.com.fiap.pratin.data.repository

import androidx.room.withTransaction
import br.com.fiap.pratin.data.local.db.PratinDatabase
import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.data.mapper.paraDominio
import br.com.fiap.pratin.domain.error.ErroRegra
import br.com.fiap.pratin.domain.error.RegraNegocioException
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.AnuncioDetalhado
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.rules.AnuncioValidator
import br.com.fiap.pratin.domain.rules.RegrasStatus
import br.com.fiap.pratin.domain.rules.agora
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

/** Anúncios de alimentos (spec/03-arquitetura.md §5.3). */
interface AlimentoRepository {
    /** Disponíveis, já ordenados (RN05). O ViewModel ainda filtra a janela aberta. */
    fun observarDisponiveis(): Flow<List<Alimento>>

    fun observarPorId(id: Long): Flow<Alimento?>

    /** Anúncios do estabelecimento: pendentes primeiro, depois os mais recentes (RF17). */
    fun observarAnuncios(cnpj: String): Flow<List<AnuncioDetalhado>>

    /** Revalida a RN03 e grava com status DISPONIVEL. Devolve o ID criado. */
    suspend fun publicar(form: NovoAnuncio, perfil: Perfil): Long

    suspend fun alternarFavorito(id: Long)

    /** RN09. */
    suspend fun cancelarAnuncio(id: Long, perfil: Perfil)

    /** RN11: expira reservas e alimentos com a janela encerrada. */
    suspend fun expirarVencidos()
}

class RoomAlimentoRepository(
    private val database: PratinDatabase,
    private val clock: Clock,
) : AlimentoRepository {

    private val alimentoDao = database.alimentoDao()
    private val reservaDao = database.reservaDao()

    override fun observarDisponiveis(): Flow<List<Alimento>> =
        alimentoDao.observarDisponiveis().map { lista -> lista.map { it.paraDominio() } }

    override fun observarPorId(id: Long): Flow<Alimento?> =
        alimentoDao.observarPorId(id).map { it?.paraDominio() }

    override fun observarAnuncios(cnpj: String): Flow<List<AnuncioDetalhado>> =
        alimentoDao.observarDoEstabelecimento(cnpj).map { lista ->
            lista.map { it.paraDominio() }.sortedWith(ORDEM_ANUNCIOS)
        }

    override suspend fun publicar(form: NovoAnuncio, perfil: Perfil): Long {
        if (!perfil.isEstabelecimento) throw RegraNegocioException(ErroRegra.APENAS_ESTABELECIMENTOS)
        val agora = clock.agora()
        if (AnuncioValidator.validar(form, agora).isNotEmpty()) {
            throw RegraNegocioException(ErroRegra.ANUNCIO_INVALIDO)
        }
        val alimento = AlimentoEntity(
            nome = form.nome.trim(),
            categoria = requireNotNull(form.categoria),
            quantidade = requireNotNull(AnuncioValidator.lerQuantidade(form.quantidadeTexto)),
            unidade = requireNotNull(form.unidade),
            dataValidade = requireNotNull(form.dataValidade),
            retiradaInicio = requireNotNull(form.retiradaInicio),
            retiradaFim = requireNotNull(form.retiradaFim),
            endereco = form.endereco.trim(),
            fotoUrl = form.fotoUrl.trim().ifEmpty { null },
            status = StatusAlimento.DISPONIVEL,
            estabelecimentoCnpj = perfil.cnpj,
            estabelecimentoNome = perfil.nome,
            declaracaoProprio = form.declaracaoProprio,
            criadoEm = agora,
            atualizadoEm = agora,
        )
        return alimentoDao.inserir(alimento)
    }

    override suspend fun alternarFavorito(id: Long) = alimentoDao.alternarFavorito(id)

    override suspend fun cancelarAnuncio(id: Long, perfil: Perfil) = database.withTransaction {
        val alimento = alimentoDao.buscarPorId(id)?.paraDominio()
            ?: throw RegraNegocioException(ErroRegra.ALIMENTO_NAO_ENCONTRADO)
        if (alimento.estabelecimentoCnpj != perfil.cnpj) throw RegraNegocioException(ErroRegra.NAO_E_DONO)
        if (!RegrasStatus.podeCancelarAnuncio(alimento)) {
            throw RegraNegocioException(ErroRegra.NAO_PODE_CANCELAR_ANUNCIO)
        }
        alimentoDao.atualizarStatus(id, StatusAlimento.CANCELADO, clock.agora())
    }

    override suspend fun expirarVencidos() {
        val agora = clock.agora()
        database.withTransaction {
            // As reservas primeiro: a subconsulta delas olha a janela do alimento.
            reservaDao.expirarVencidas(agora)
            alimentoDao.expirarVencidos(agora)
        }
    }

    private companion object {
        val ORDEM_ANUNCIOS = compareByDescending<AnuncioDetalhado> { it.alimento.status.pendente }
            .thenByDescending { it.alimento.criadoEm }
    }
}
