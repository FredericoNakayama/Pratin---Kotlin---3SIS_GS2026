package br.com.fiap.pratin.domain

import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.Reserva
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.model.Unidade
import java.time.LocalDate
import java.time.LocalDateTime

/** "Agora" fixo usado em todos os testes de domínio: sexta, 14/11/2026 às 10:00. */
val AGORA: LocalDateTime = LocalDateTime.of(2026, 11, 14, 10, 0)
val HOJE: LocalDate = AGORA.toLocalDate()

fun alimentoDeTeste(
    status: StatusAlimento = StatusAlimento.DISPONIVEL,
    retiradaInicio: LocalDateTime = AGORA.plusHours(2),
    retiradaFim: LocalDateTime = AGORA.plusHours(5),
) = Alimento(
    id = 1,
    nome = "Pão de forma",
    categoria = Categoria.PAES,
    quantidade = 12.0,
    unidade = Unidade.PACOTE,
    dataValidade = HOJE.plusDays(2),
    retiradaInicio = retiradaInicio,
    retiradaFim = retiradaFim,
    endereco = "Rua das Flores, 120",
    fotoUrl = null,
    status = status,
    estabelecimentoCnpj = "11222333000181",
    estabelecimentoNome = "Padaria Pão Quente",
    declaracaoProprio = true,
    favorito = false,
    criadoEm = AGORA.minusHours(1),
    atualizadoEm = AGORA.minusHours(1),
)

fun reservaDeTeste(status: StatusReserva = StatusReserva.ATIVA) = Reserva(
    id = 1,
    alimentoId = 1,
    ongCnpj = "12345678000195",
    ongNome = "ONG Mesa Cheia",
    codigoRetirada = "PX7K2Q",
    status = status,
    criadaEm = AGORA,
    atualizadaEm = AGORA,
)

/** Anúncio válido em [AGORA]; cada teste altera só o campo que quer quebrar. */
fun anuncioValido() = NovoAnuncio(
    nome = "Pão de forma",
    categoria = Categoria.PAES,
    quantidadeTexto = "12",
    unidade = Unidade.PACOTE,
    dataValidade = HOJE.plusDays(2),
    retiradaInicio = AGORA.plusHours(2),
    retiradaFim = AGORA.plusHours(5),
    endereco = "Rua das Flores, 120",
    fotoUrl = "",
    declaracaoProprio = true,
)
