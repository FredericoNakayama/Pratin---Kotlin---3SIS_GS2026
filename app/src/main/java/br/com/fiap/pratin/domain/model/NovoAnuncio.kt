package br.com.fiap.pratin.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/** Dados digitados no formulário de anúncio, ainda sem validação (ver AnuncioValidator). */
data class NovoAnuncio(
    val nome: String = "",
    val categoria: Categoria? = null,
    val quantidadeTexto: String = "",
    val unidade: Unidade? = null,
    val dataValidade: LocalDate? = null,
    val retiradaInicio: LocalDateTime? = null,
    val retiradaFim: LocalDateTime? = null,
    val endereco: String = "",
    val fotoUrl: String = "",
    val declaracaoProprio: Boolean = false,
)
