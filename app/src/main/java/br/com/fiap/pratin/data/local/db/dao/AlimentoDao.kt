package br.com.fiap.pratin.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import br.com.fiap.pratin.data.local.db.entity.AlimentoComReservas
import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.domain.model.StatusAlimento
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface AlimentoDao {

    /** Anúncios disponíveis, ordenados pela validade e pelo fim da retirada (RN05). */
    @Query("SELECT * FROM alimentos WHERE status = 'DISPONIVEL' ORDER BY dataValidade, retiradaFim")
    fun observarDisponiveis(): Flow<List<AlimentoEntity>>

    @Query("SELECT * FROM alimentos WHERE id = :id")
    fun observarPorId(id: Long): Flow<AlimentoEntity?>

    @Query("SELECT * FROM alimentos WHERE id = :id")
    suspend fun buscarPorId(id: Long): AlimentoEntity?

    @Transaction
    @Query("SELECT * FROM alimentos WHERE estabelecimentoCnpj = :cnpj")
    fun observarDoEstabelecimento(cnpj: String): Flow<List<AlimentoComReservas>>

    @Insert
    suspend fun inserir(alimento: AlimentoEntity): Long

    @Insert
    suspend fun inserirTodos(alimentos: List<AlimentoEntity>): List<Long>

    @Query("UPDATE alimentos SET status = :status, atualizadoEm = :agora WHERE id = :id")
    suspend fun atualizarStatus(id: Long, status: StatusAlimento, agora: LocalDateTime)

    /** Favoritar não altera `atualizadoEm` (RN12). */
    @Query("UPDATE alimentos SET favorito = NOT favorito WHERE id = :id")
    suspend fun alternarFavorito(id: Long)

    /** RN11: pendentes com a janela encerrada viram EXPIRADO. Devolve quantos mudaram. */
    @Query(
        """
        UPDATE alimentos SET status = 'EXPIRADO', atualizadoEm = :agora
        WHERE status IN ('DISPONIVEL', 'RESERVADO') AND retiradaFim <= :agora
        """,
    )
    suspend fun expirarVencidos(agora: LocalDateTime): Int

    @Query("SELECT COUNT(*) FROM alimentos")
    suspend fun contar(): Int
}
