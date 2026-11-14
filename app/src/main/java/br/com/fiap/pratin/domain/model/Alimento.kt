package br.com.fiap.pratin.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Anúncio de um lote de alimento publicado por um estabelecimento. */
data class Alimento(
    val id: Long,
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
    val favorito: Boolean,
    val criadoEm: LocalDateTime,
    val atualizadoEm: LocalDateTime,
) {
    /** A janela de retirada ainda não terminou (`agora < retiradaFim`). */
    fun janelaAberta(agora: LocalDateTime): Boolean = agora.isBefore(retiradaFim)
}
