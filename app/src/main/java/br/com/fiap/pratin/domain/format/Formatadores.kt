package br.com.fiap.pratin.domain.format

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.Normalizer
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

/** Situação do vencimento de um lote (RN14). A UI converte em texto. */
sealed interface Vencimento {
    data object Hoje : Vencimento
    data object Amanha : Vencimento
    data class EmDias(val dias: Int) : Vencimento
    data object Vencido : Vencimento
}

/** Formatação de datas, quantidades e CNPJ no padrão brasileiro (RN15). */
object Formatadores {
    private val PT_BR: Locale = Locale.forLanguageTag("pt-BR")
    private val DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy", PT_BR)
    private val DIA_MES = DateTimeFormatter.ofPattern("dd/MM", PT_BR)
    private val HORA = DateTimeFormatter.ofPattern("HH:mm", PT_BR)
    private val MARCAS_DIACRITICAS = "\\p{Mn}+".toRegex()

    fun vencimento(validade: LocalDate, hoje: LocalDate): Vencimento {
        val dias = ChronoUnit.DAYS.between(hoje, validade)
        return when {
            dias < 0 -> Vencimento.Vencido
            dias == 0L -> Vencimento.Hoje
            dias == 1L -> Vencimento.Amanha
            else -> Vencimento.EmDias(dias.toInt())
        }
    }

    /** `15.0 → "15"`, `8.5 → "8,5"`. */
    fun quantidade(valor: Double): String =
        DecimalFormat("0.##", DecimalFormatSymbols(PT_BR)).format(valor)

    fun data(data: LocalDate): String = data.format(DATA)

    fun diaMes(data: LocalDate): String = data.format(DIA_MES)

    fun hora(dataHora: LocalDateTime): String = dataHora.format(HORA)

    /** `15/11 11:00–14:00` no mesmo dia; `15/11 14:00 – 16/11 10:00` em dias diferentes. */
    fun janela(inicio: LocalDateTime, fim: LocalDateTime): String =
        if (inicio.toLocalDate() == fim.toLocalDate()) {
            "${diaMes(inicio.toLocalDate())} ${hora(inicio)}–${hora(fim)}"
        } else {
            "${diaMes(inicio.toLocalDate())} ${hora(inicio)} – ${diaMes(fim.toLocalDate())} ${hora(fim)}"
        }

    /** `11222333000181 → 11.222.333/0001-81`. Texto que não tem 14 dígitos volta sem máscara. */
    fun cnpj(digitos: String): String {
        if (digitos.length != 14 || !digitos.all(Char::isDigit)) return digitos
        return "${digitos.substring(0, 2)}.${digitos.substring(2, 5)}.${digitos.substring(5, 8)}/" +
            "${digitos.substring(8, 12)}-${digitos.substring(12)}"
    }

    /** Minúsculas, sem acentos e sem espaços nas pontas, para a busca do Início (RF07). */
    fun normalizarBusca(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(PT_BR), Normalizer.Form.NFD)
            .replace(MARCAS_DIACRITICAS, "")
}
