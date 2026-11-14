package br.com.fiap.pratin.data.local.db

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Datas gravadas como TEXT em formato fixo.
 *
 * O padrão `yyyy-MM-dd'T'HH:mm:ss` SEMPRE inclui os segundos: `LocalDateTime.toString()` os omite
 * quando são zero, o que quebraria a comparação de texto no SQL (`retiradaFim <= :agora`).
 * Enums usam o conversor nativo do Room (nome do enum como TEXT).
 */
class Converters {

    @TypeConverter
    fun dataParaTexto(data: LocalDate?): String? = data?.format(DATA)

    @TypeConverter
    fun textoParaData(texto: String?): LocalDate? = texto?.let { LocalDate.parse(it, DATA) }

    @TypeConverter
    fun dataHoraParaTexto(dataHora: LocalDateTime?): String? = dataHora?.format(DATA_HORA)

    @TypeConverter
    fun textoParaDataHora(texto: String?): LocalDateTime? = texto?.let { LocalDateTime.parse(it, DATA_HORA) }

    companion object {
        val DATA: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val DATA_HORA: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
    }
}
