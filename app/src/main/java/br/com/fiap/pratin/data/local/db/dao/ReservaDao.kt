package br.com.fiap.pratin.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import br.com.fiap.pratin.data.local.db.entity.ReservaComAlimento
import br.com.fiap.pratin.data.local.db.entity.ReservaEntity
import br.com.fiap.pratin.domain.model.StatusReserva
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ReservaDao {

    @Transaction
    @Query("SELECT * FROM reservas WHERE ongCnpj = :cnpj")
    fun observarDaOng(cnpj: String): Flow<List<ReservaComAlimento>>

    @Transaction
    @Query("SELECT * FROM reservas WHERE id = :id")
    fun observarPorId(id: Long): Flow<ReservaComAlimento?>

    @Query("SELECT * FROM reservas WHERE id = :id")
    suspend fun buscarPorId(id: Long): ReservaEntity?

    @Query("SELECT * FROM reservas WHERE alimentoId = :alimentoId AND status = 'ATIVA' LIMIT 1")
    suspend fun buscarAtivaDoAlimento(alimentoId: Long): ReservaEntity?

    @Insert
    suspend fun inserir(reserva: ReservaEntity): Long

    @Insert
    suspend fun inserirTodas(reservas: List<ReservaEntity>): List<Long>

    @Query("UPDATE reservas SET status = :status, atualizadaEm = :agora WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: StatusReserva, agora: LocalDateTime)

    /** RN11: reservas ATIVAS cujo alimento teve a janela encerrada viram EXPIRADA. */
    @Query(
        """
        UPDATE reservas SET status = 'EXPIRADA', atualizadaEm = :agora
        WHERE status = 'ATIVA'
          AND alimentoId IN (SELECT id FROM alimentos WHERE retiradaFim <= :agora)
        """,
    )
    suspend fun expirarVencidas(agora: LocalDateTime): Int
}
