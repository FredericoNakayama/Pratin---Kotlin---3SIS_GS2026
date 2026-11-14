package br.com.fiap.pratin.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.Unidade
import java.time.LocalDate
import java.time.LocalDateTime

/** Tabela `alimentos`: cada linha é um anúncio (lote) publicado por um estabelecimento. */
@Entity(
    tableName = "alimentos",
    indices = [Index("status"), Index("estabelecimentoCnpj")],
)
data class AlimentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val categoria: Categoria,
    val quantidade: Double,
    val unidade: Unidade,
    val dataValidade: LocalDate,
    val retiradaInicio: LocalDateTime,
    val retiradaFim: LocalDateTime,
    val endereco: String,
    val fotoUrl: String?,
    val status: StatusAlimento,
    val estabelecimentoCnpj: String,
    val estabelecimentoNome: String,
    val declaracaoProprio: Boolean,
    @ColumnInfo(defaultValue = "0") val favorito: Boolean = false,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
)
