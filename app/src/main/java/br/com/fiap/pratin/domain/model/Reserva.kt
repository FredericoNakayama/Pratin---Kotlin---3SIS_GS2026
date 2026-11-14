package br.com.fiap.pratin.domain.model

import java.time.LocalDateTime

/** Reserva de um lote inteiro por uma ONG (RN06). */
data class Reserva(
    val id: Long,
    val alimentoId: Long,
    val ongCnpj: String,
    val ongNome: String,
    val codigoRetirada: String,
    val status: StatusReserva,
    val criadaEm: LocalDateTime,
    val atualizadaEm: LocalDateTime,
)

/** Reserva com o alimento reservado (Atividades da ONG e Confirmação). */
data class ReservaDetalhada(val reserva: Reserva, val alimento: Alimento)

/** Anúncio com a reserva ativa, se houver (Atividades do estabelecimento). */
data class AnuncioDetalhado(val alimento: Alimento, val reservaAtiva: Reserva?)
