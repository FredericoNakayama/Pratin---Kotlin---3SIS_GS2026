package br.com.fiap.pratin.domain.format

import br.com.fiap.pratin.domain.HOJE
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

/** RN14 e RN15 — formatação de datas, quantidades, CNPJ e vencimento. */
class FormatadoresTest {

    @Test
    fun `situação do vencimento`() {
        assertEquals(Vencimento.Hoje, Formatadores.vencimento(HOJE, HOJE))
        assertEquals(Vencimento.Amanha, Formatadores.vencimento(HOJE.plusDays(1), HOJE))
        assertEquals(Vencimento.EmDias(3), Formatadores.vencimento(HOJE.plusDays(3), HOJE))
        assertEquals(Vencimento.Vencido, Formatadores.vencimento(HOJE.minusDays(1), HOJE))
    }

    @Test
    fun `quantidade inteira sem casas e decimal com vírgula`() {
        assertEquals("15", Formatadores.quantidade(15.0))
        assertEquals("8,5", Formatadores.quantidade(8.5))
        assertEquals("0,25", Formatadores.quantidade(0.25))
    }

    @Test
    fun `datas no formato brasileiro`() {
        assertEquals("09/11/2026", Formatadores.data(LocalDate.of(2026, 11, 9)))
        assertEquals("09/11", Formatadores.diaMes(LocalDate.of(2026, 11, 9)))
        assertEquals("07:05", Formatadores.hora(LocalDateTime.of(2026, 11, 9, 7, 5)))
    }

    @Test
    fun `janela no mesmo dia`() {
        val inicio = LocalDateTime.of(2026, 11, 15, 11, 0)
        assertEquals("15/11 11:00–14:00", Formatadores.janela(inicio, inicio.withHour(14)))
    }

    @Test
    fun `janela em dias diferentes`() {
        val inicio = LocalDateTime.of(2026, 11, 15, 14, 0)
        val fim = LocalDateTime.of(2026, 11, 16, 10, 0)
        assertEquals("15/11 14:00 – 16/11 10:00", Formatadores.janela(inicio, fim))
    }

    @Test
    fun `CNPJ com máscara`() {
        assertEquals("11.222.333/0001-81", Formatadores.cnpj("11222333000181"))
        assertEquals("123", Formatadores.cnpj("123"))
    }

    @Test
    fun `normaliza texto para busca sem acento e sem maiúsculas`() {
        assertEquals("pao de queijo", Formatadores.normalizarBusca("  Pão de QUEIJO "))
        assertEquals("feijao", Formatadores.normalizarBusca("Feijão"))
    }
}
