package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.Reserva
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import java.time.LocalDateTime

/**
 * Transições permitidas das máquinas de estado (spec/02-requisitos.md §3).
 * Os repositórios chamam estas funções dentro da transação antes de alterar o status.
 */
object RegrasStatus {

    /** Lista do Início (RN05). */
    fun apareceNoInicio(alimento: Alimento, agora: LocalDateTime): Boolean =
        alimento.status == StatusAlimento.DISPONIVEL && alimento.janelaAberta(agora)

    /** DISPONIVEL → RESERVADO (RN06). */
    fun podeReservar(alimento: Alimento, agora: LocalDateTime): Boolean =
        alimento.status == StatusAlimento.DISPONIVEL && alimento.janelaAberta(agora)

    /** DISPONIVEL → CANCELADO (RN09). */
    fun podeCancelarAnuncio(alimento: Alimento): Boolean =
        alimento.status == StatusAlimento.DISPONIVEL

    /** Reserva ATIVA → CANCELADA e alimento RESERVADO → DISPONIVEL (RN08). */
    fun podeCancelarReserva(reserva: Reserva, alimento: Alimento, agora: LocalDateTime): Boolean =
        reserva.status == StatusReserva.ATIVA && alimento.janelaAberta(agora)

    /** RESERVADO → RETIRADO e reserva ATIVA → CONCLUIDA (RN10). */
    fun podeConfirmarRetirada(alimento: Alimento, agora: LocalDateTime): Boolean =
        alimento.status == StatusAlimento.RESERVADO && alimento.janelaAberta(agora)

    /** DISPONIVEL/RESERVADO → EXPIRADO quando a janela termina (RN11). */
    fun deveExpirar(alimento: Alimento, agora: LocalDateTime): Boolean =
        alimento.status.pendente && !alimento.janelaAberta(agora)
}
