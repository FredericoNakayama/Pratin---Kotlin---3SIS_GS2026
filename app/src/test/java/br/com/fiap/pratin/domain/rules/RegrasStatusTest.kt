package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.AGORA
import br.com.fiap.pratin.domain.alimentoDeTeste
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.reservaDeTeste
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Máquinas de estado (spec/02-requisitos.md §3) e RN05, RN06, RN08–RN11. */
class RegrasStatusTest {

    private val janelaEncerrada = alimentoDeTeste(
        retiradaInicio = AGORA.minusHours(3),
        retiradaFim = AGORA,
    )

    @Test
    fun `só reserva alimento disponível com janela aberta`() {
        assertTrue(RegrasStatus.podeReservar(alimentoDeTeste(), AGORA))
        StatusAlimento.entries.filter { it != StatusAlimento.DISPONIVEL }.forEach {
            assertFalse(it.name, RegrasStatus.podeReservar(alimentoDeTeste(status = it), AGORA))
        }
        assertFalse(RegrasStatus.podeReservar(janelaEncerrada, AGORA))
    }

    @Test
    fun `só cancela anúncio disponível`() {
        StatusAlimento.entries.forEach {
            assertEquals(it.name, it == StatusAlimento.DISPONIVEL, RegrasStatus.podeCancelarAnuncio(alimentoDeTeste(status = it)))
        }
    }

    @Test
    fun `só cancela reserva ativa com janela aberta`() {
        val alimento = alimentoDeTeste(status = StatusAlimento.RESERVADO)
        assertTrue(RegrasStatus.podeCancelarReserva(reservaDeTeste(), alimento, AGORA))
        StatusReserva.entries.filter { it != StatusReserva.ATIVA }.forEach {
            assertFalse(it.name, RegrasStatus.podeCancelarReserva(reservaDeTeste(status = it), alimento, AGORA))
        }
        assertFalse(
            RegrasStatus.podeCancelarReserva(
                reservaDeTeste(),
                janelaEncerrada.copy(status = StatusAlimento.RESERVADO),
                AGORA,
            ),
        )
    }

    @Test
    fun `só confirma retirada de alimento reservado com janela aberta`() {
        assertTrue(RegrasStatus.podeConfirmarRetirada(alimentoDeTeste(status = StatusAlimento.RESERVADO), AGORA))
        assertFalse(RegrasStatus.podeConfirmarRetirada(alimentoDeTeste(status = StatusAlimento.DISPONIVEL), AGORA))
        assertFalse(
            RegrasStatus.podeConfirmarRetirada(janelaEncerrada.copy(status = StatusAlimento.RESERVADO), AGORA),
        )
    }

    @Test
    fun `expira só alimento pendente com janela encerrada`() {
        assertTrue(RegrasStatus.deveExpirar(janelaEncerrada, AGORA))
        assertTrue(RegrasStatus.deveExpirar(janelaEncerrada.copy(status = StatusAlimento.RESERVADO), AGORA))
        assertFalse(RegrasStatus.deveExpirar(janelaEncerrada.copy(status = StatusAlimento.RETIRADO), AGORA))
        assertFalse(RegrasStatus.deveExpirar(alimentoDeTeste(), AGORA))
    }

    @Test
    fun `janela fecha exatamente no fim da retirada`() {
        val alimento = alimentoDeTeste()
        assertTrue(alimento.janelaAberta(alimento.retiradaFim.minusSeconds(1)))
        assertFalse(alimento.janelaAberta(alimento.retiradaFim))
    }

    @Test
    fun `aparece no início só disponível com janela aberta`() {
        assertTrue(RegrasStatus.apareceNoInicio(alimentoDeTeste(), AGORA))
        assertFalse(RegrasStatus.apareceNoInicio(alimentoDeTeste(status = StatusAlimento.RESERVADO), AGORA))
        assertFalse(RegrasStatus.apareceNoInicio(janelaEncerrada, AGORA))
    }
}
