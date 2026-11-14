package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.error.CampoAnuncio
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.NovoAnuncio
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/** Regras de um anúncio válido (RN03). Devolve um erro por campo; mapa vazio = válido. */
object AnuncioValidator {
    const val NOME_MINIMO = 2
    const val NOME_MAXIMO = 60
    const val ENDERECO_MINIMO = 5
    const val DIAS_MAXIMOS_VALIDADE = 7L
    val JANELA_MINIMA: Duration = Duration.ofHours(1)
    val TOLERANCIA_INICIO: Duration = Duration.ofMinutes(5)

    fun validar(form: NovoAnuncio, agora: LocalDateTime): Map<CampoAnuncio, ErroCampo> = buildMap {
        validarNome(form.nome)?.let { put(CampoAnuncio.NOME, it) }
        if (form.categoria == null) put(CampoAnuncio.CATEGORIA, ErroCampo.CATEGORIA)
        validarQuantidade(form.quantidadeTexto)?.let { put(CampoAnuncio.QUANTIDADE, it) }
        if (form.unidade == null) put(CampoAnuncio.UNIDADE, ErroCampo.UNIDADE)
        validarValidade(form.dataValidade, agora.toLocalDate())?.let { put(CampoAnuncio.VALIDADE, it) }
        validarJanela(form.retiradaInicio, form.retiradaFim, form.dataValidade, agora)
            ?.let { put(CampoAnuncio.JANELA, it) }
        if (form.endereco.trim().length < ENDERECO_MINIMO) put(CampoAnuncio.ENDERECO, ErroCampo.ENDERECO)
        validarFotoUrl(form.fotoUrl)?.let { put(CampoAnuncio.FOTO_URL, it) }
        if (!form.declaracaoProprio) put(CampoAnuncio.DECLARACAO, ErroCampo.DECLARACAO)
    }

    /** Converte o texto digitado (aceita vírgula ou ponto) em quantidade. */
    fun lerQuantidade(texto: String): Double? = texto.trim().replace(',', '.').toDoubleOrNull()

    /** Último instante em que a retirada pode terminar: 23:59:59 do dia da validade. */
    fun limiteDaJanela(validade: LocalDate): LocalDateTime = validade.atTime(LocalTime.of(23, 59, 59))

    private fun validarNome(nome: String): ErroCampo? =
        if (nome.trim().length !in NOME_MINIMO..NOME_MAXIMO) ErroCampo.NOME_ALIMENTO else null

    private fun validarQuantidade(texto: String): ErroCampo? {
        val quantidade = lerQuantidade(texto)
        return if (quantidade == null || quantidade <= 0.0) ErroCampo.QUANTIDADE else null
    }

    private fun validarValidade(validade: LocalDate?, hoje: LocalDate): ErroCampo? {
        if (validade == null) return ErroCampo.VALIDADE_OBRIGATORIA
        val dias = ChronoUnit.DAYS.between(hoje, validade)
        return if (dias !in 0..DIAS_MAXIMOS_VALIDADE) ErroCampo.VALIDADE_FORA_DO_PRAZO else null
    }

    /** Ordem: obrigatória → no passado → curta → após a validade (spec/03-arquitetura.md §4.2). */
    private fun validarJanela(
        inicio: LocalDateTime?,
        fim: LocalDateTime?,
        validade: LocalDate?,
        agora: LocalDateTime,
    ): ErroCampo? {
        if (inicio == null || fim == null) return ErroCampo.JANELA_OBRIGATORIA
        if (!fim.isAfter(agora) || inicio.isBefore(agora.minus(TOLERANCIA_INICIO))) {
            return ErroCampo.JANELA_NO_PASSADO
        }
        if (Duration.between(inicio, fim) < JANELA_MINIMA) return ErroCampo.JANELA_CURTA
        if (validade != null && fim.isAfter(limiteDaJanela(validade))) return ErroCampo.JANELA_APOS_VALIDADE
        return null
    }

    private fun validarFotoUrl(url: String): ErroCampo? {
        val valor = url.trim()
        val valida = valor.isEmpty() || valor.startsWith("http://") || valor.startsWith("https://")
        return if (valida) null else ErroCampo.FOTO_URL
    }
}
