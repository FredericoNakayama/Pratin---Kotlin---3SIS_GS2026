package br.com.fiap.pratin.data.local.db.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import br.com.fiap.pratin.domain.model.StatusReserva
import java.time.LocalDateTime

/** Tabela `reservas`: reserva de um lote inteiro por uma ONG. */
@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["alimentoId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("alimentoId"), Index("ongCnpj")],
)
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val alimentoId: Long,
    val ongCnpj: String,
    val ongNome: String,
    val codigoRetirada: String,
    val status: StatusReserva,
    val criadaEm: LocalDateTime,
    val atualizadaEm: LocalDateTime,
)

/** Reserva com o alimento reservado. */
data class ReservaComAlimento(
    @Embedded val reserva: ReservaEntity,
    @Relation(parentColumn = "alimentoId", entityColumn = "id")
    val alimento: AlimentoEntity,
)

/** Alimento com todas as suas reservas (a UI usa a ATIVA). */
data class AlimentoComReservas(
    @Embedded val alimento: AlimentoEntity,
    @Relation(parentColumn = "id", entityColumn = "alimentoId")
    val reservas: List<ReservaEntity>,
)
