package br.com.fiap.pratin.data.repository

import androidx.room.withTransaction
import br.com.fiap.pratin.data.local.db.PratinDatabase
import br.com.fiap.pratin.data.local.db.entity.ReservaEntity
import br.com.fiap.pratin.data.mapper.paraDominio
import br.com.fiap.pratin.domain.error.ErroRegra
import br.com.fiap.pratin.domain.error.RegraNegocioException
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.rules.CodigoGenerator
import br.com.fiap.pratin.domain.rules.RegrasStatus
import br.com.fiap.pratin.domain.rules.agora
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Clock

/** Reservas, cancelamentos e retirada (spec/03-arquitetura.md §5.3). */
interface ReservaRepository {
    /** Reservas da ONG: ATIVA primeiro, depois as mais recentes (RF15). */
    fun observarDaOng(cnpj: String): Flow<List<ReservaDetalhada>>

    fun observarPorId(id: Long): Flow<ReservaDetalhada?>

    /** RN06. Devolve o ID da reserva criada. */
    suspend fun reservar(alimentoId: Long, perfil: Perfil): Long

    /** RN08. */
    suspend fun cancelar(reservaId: Long, perfil: Perfil)

    /** RN10. */
    suspend fun confirmarRetirada(alimentoId: Long, codigo: String, perfil: Perfil)
}

class RoomReservaRepository(
    private val database: PratinDatabase,
    private val clock: Clock,
    private val gerarCodigo: () -> String = { CodigoGenerator.gerar() },
) : ReservaRepository {

    private val alimentoDao = database.alimentoDao()
    private val reservaDao = database.reservaDao()

    override fun observarDaOng(cnpj: String): Flow<List<ReservaDetalhada>> =
        reservaDao.observarDaOng(cnpj).map { lista ->
            lista.map { it.paraDominio() }.sortedWith(ORDEM_RESERVAS)
        }

    override fun observarPorId(id: Long): Flow<ReservaDetalhada?> =
        reservaDao.observarPorId(id).map { it?.paraDominio() }

    /** Tudo em uma transação: se a reserva não puder ser criada, o alimento não muda. */
    override suspend fun reservar(alimentoId: Long, perfil: Perfil): Long {
        if (!perfil.isOng) throw RegraNegocioException(ErroRegra.APENAS_ONGS)
        return database.withTransaction {
            val agora = clock.agora()
            val alimento = alimentoDao.buscarPorId(alimentoId)?.paraDominio()
                ?: throw RegraNegocioException(ErroRegra.ALIMENTO_NAO_ENCONTRADO)
            if (alimento.status != StatusAlimento.DISPONIVEL) throw RegraNegocioException(ErroRegra.JA_RESERVADO)
            if (!RegrasStatus.podeReservar(alimento, agora)) throw RegraNegocioException(ErroRegra.PRAZO_ENCERRADO)

            alimentoDao.atualizarStatus(alimentoId, StatusAlimento.RESERVADO, agora)
            reservaDao.inserir(
                ReservaEntity(
                    alimentoId = alimentoId,
                    ongCnpj = perfil.cnpj,
                    ongNome = perfil.nome,
                    codigoRetirada = gerarCodigo(),
                    status = StatusReserva.ATIVA,
                    criadaEm = agora,
                    atualizadaEm = agora,
                ),
            )
        }
    }

    override suspend fun cancelar(reservaId: Long, perfil: Perfil) = database.withTransaction {
        val agora = clock.agora()
        val reserva = reservaDao.buscarPorId(reservaId)?.paraDominio()
            ?: throw RegraNegocioException(ErroRegra.RESERVA_NAO_ENCONTRADA)
        if (reserva.ongCnpj != perfil.cnpj) throw RegraNegocioException(ErroRegra.NAO_E_DONO)
        val alimento = alimentoDao.buscarPorId(reserva.alimentoId)?.paraDominio()
            ?: throw RegraNegocioException(ErroRegra.ALIMENTO_NAO_ENCONTRADO)
        if (!RegrasStatus.podeCancelarReserva(reserva, alimento, agora)) {
            throw RegraNegocioException(ErroRegra.NAO_PODE_CANCELAR_RESERVA)
        }
        reservaDao.atualizarStatus(reservaId, StatusReserva.CANCELADA, agora)
        if (alimento.status == StatusAlimento.RESERVADO) {
            alimentoDao.atualizarStatus(alimento.id, StatusAlimento.DISPONIVEL, agora)
        }
    }

    override suspend fun confirmarRetirada(alimentoId: Long, codigo: String, perfil: Perfil) {
        if (codigo.trim().length != CodigoGenerator.TAMANHO) throw RegraNegocioException(ErroRegra.CODIGO_TAMANHO)
        database.withTransaction {
            val agora = clock.agora()
            val alimento = alimentoDao.buscarPorId(alimentoId)?.paraDominio()
                ?: throw RegraNegocioException(ErroRegra.ALIMENTO_NAO_ENCONTRADO)
            if (alimento.estabelecimentoCnpj != perfil.cnpj) throw RegraNegocioException(ErroRegra.NAO_E_DONO)
            if (alimento.status != StatusAlimento.RESERVADO) {
                throw RegraNegocioException(ErroRegra.RETIRADA_INDISPONIVEL)
            }
            if (!RegrasStatus.podeConfirmarRetirada(alimento, agora)) {
                throw RegraNegocioException(ErroRegra.PRAZO_ENCERRADO)
            }
            val reserva = reservaDao.buscarAtivaDoAlimento(alimentoId)
                ?: throw RegraNegocioException(ErroRegra.RESERVA_NAO_ENCONTRADA)
            if (!CodigoGenerator.confere(codigo, reserva.codigoRetirada)) {
                throw RegraNegocioException(ErroRegra.CODIGO_INVALIDO)
            }
            reservaDao.atualizarStatus(reserva.id, StatusReserva.CONCLUIDA, agora)
            alimentoDao.atualizarStatus(alimentoId, StatusAlimento.RETIRADO, agora)
        }
    }

    private companion object {
        val ORDEM_RESERVAS = compareByDescending<ReservaDetalhada> { it.reserva.status == StatusReserva.ATIVA }
            .thenByDescending { it.reserva.criadaEm }
    }
}
