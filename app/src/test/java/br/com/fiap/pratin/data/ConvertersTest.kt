package br.com.fiap.pratin.data

import br.com.fiap.pratin.data.local.db.Converters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun `data e hora sempre gravam os segundos`() {
        assertEquals("2026-11-14T10:00:00", converters.dataHoraParaTexto(LocalDateTime.of(2026, 11, 14, 10, 0)))
    }

    @Test
    fun `frações de segundo são descartadas`() {
        val comNanos = LocalDateTime.of(2026, 11, 14, 10, 0, 5, 123_456_789)
        assertEquals("2026-11-14T10:00:05", converters.dataHoraParaTexto(comNanos))
    }

    @Test
    fun `ida e volta preserva data e data-hora`() {
        val data = LocalDate.of(2026, 11, 9)
        val dataHora = LocalDateTime.of(2026, 11, 9, 7, 5, 30)
        assertEquals(data, converters.textoParaData(converters.dataParaTexto(data)))
        assertEquals(dataHora, converters.textoParaDataHora(converters.dataHoraParaTexto(dataHora)))
    }

    @Test
    fun `texto gravado ordena igual ao tempo`() {
        val antes = converters.dataHoraParaTexto(LocalDateTime.of(2026, 11, 14, 9, 59, 59))!!
        val depois = converters.dataHoraParaTexto(LocalDateTime.of(2026, 11, 14, 10, 0))!!
        assertTrue(antes < depois)
    }
}
