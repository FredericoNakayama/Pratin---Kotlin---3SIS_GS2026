package br.com.fiap.pratin.data.mapper

import br.com.fiap.pratin.data.local.db.entity.AlimentoComReservas
import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.data.local.db.entity.ReservaComAlimento
import br.com.fiap.pratin.data.local.db.entity.ReservaEntity
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.AnuncioDetalhado
import br.com.fiap.pratin.domain.model.Reserva
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.domain.model.StatusReserva

// Conversões entre entidades do Room e modelos do domínio. A UI só conhece o domínio.

fun AlimentoEntity.paraDominio() = Alimento(
    id = id,
    nome = nome,
    categoria = categoria,
    quantidade = quantidade,
    unidade = unidade,
    dataValidade = dataValidade,
    retiradaInicio = retiradaInicio,
    retiradaFim = retiradaFim,
    endereco = endereco,
    fotoUrl = fotoUrl,
    status = status,
    estabelecimentoCnpj = estabelecimentoCnpj,
    estabelecimentoNome = estabelecimentoNome,
    declaracaoProprio = declaracaoProprio,
    favorito = favorito,
    criadoEm = criadoEm,
    atualizadoEm = atualizadoEm,
)

fun Alimento.paraEntidade() = AlimentoEntity(
    id = id,
    nome = nome,
    categoria = categoria,
    quantidade = quantidade,
    unidade = unidade,
    dataValidade = dataValidade,
    retiradaInicio = retiradaInicio,
    retiradaFim = retiradaFim,
    endereco = endereco,
    fotoUrl = fotoUrl,
    status = status,
    estabelecimentoCnpj = estabelecimentoCnpj,
    estabelecimentoNome = estabelecimentoNome,
    declaracaoProprio = declaracaoProprio,
    favorito = favorito,
    criadoEm = criadoEm,
    atualizadoEm = atualizadoEm,
)

fun ReservaEntity.paraDominio() = Reserva(
    id = id,
    alimentoId = alimentoId,
    ongCnpj = ongCnpj,
    ongNome = ongNome,
    codigoRetirada = codigoRetirada,
    status = status,
    criadaEm = criadaEm,
    atualizadaEm = atualizadaEm,
)

fun ReservaComAlimento.paraDominio() = ReservaDetalhada(
    reserva = reserva.paraDominio(),
    alimento = alimento.paraDominio(),
)

fun AlimentoComReservas.paraDominio() = AnuncioDetalhado(
    alimento = alimento.paraDominio(),
    reservaAtiva = reservas.firstOrNull { it.status == StatusReserva.ATIVA }?.paraDominio(),
)
