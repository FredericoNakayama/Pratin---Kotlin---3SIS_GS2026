package br.com.fiap.pratin.data.seed

import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.data.local.db.entity.ReservaEntity
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.Categoria.HORTIFRUTI
import br.com.fiap.pratin.domain.model.Categoria.LATICINIOS
import br.com.fiap.pratin.domain.model.Categoria.MERCEARIA
import br.com.fiap.pratin.domain.model.Categoria.PAES
import br.com.fiap.pratin.domain.model.Categoria.REFEICOES
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.model.TipoPerfil
import br.com.fiap.pratin.domain.model.Unidade
import br.com.fiap.pratin.domain.model.Unidade.KG
import br.com.fiap.pratin.domain.model.Unidade.PACOTE
import br.com.fiap.pratin.domain.model.Unidade.PORCAO
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Dados de exemplo gravados na primeira execução (RN16, spec/03-arquitetura.md §8).
 * Datas relativas ao dia atual; CNPJs fictícios, mas válidos pela RN02 (os mesmos do app Flutter).
 */
object SeedData {

    /** Código fixo da reserva de demonstração (permite mostrar "Confirmar retirada" no 1º uso). */
    const val CODIGO_DEMO = "PX7K2Q"
    private const val CODIGO_HISTORICO = "K3M9TR"

    val padaria = Perfil(TipoPerfil.ESTABELECIMENTO, "Padaria Pão Quente", "11222333000181", "Rua das Flores, 120 – São Paulo/SP")
    val mercado = Perfil(TipoPerfil.ESTABELECIMENTO, "Mercado Bom Preço", "11444777000161", "Av. Paulista, 900 – São Paulo/SP")
    val restaurante = Perfil(TipoPerfil.ESTABELECIMENTO, "Restaurante Sabor Caseiro", "98765432000198", "Rua Augusta, 55 – São Paulo/SP")
    val supermercado = Perfil(TipoPerfil.ESTABELECIMENTO, "Supermercado Economia", "33444555000181", "Rua Voluntários da Pátria, 300 – São Paulo/SP")
    val mercearia = Perfil(TipoPerfil.ESTABELECIMENTO, "Mercearia São José", "55666777000181", "Rua Domingos de Morais, 1500 – São Paulo/SP")
    val ongMesaCheia = Perfil(TipoPerfil.ONG, "ONG Mesa Cheia", "12345678000195", "Rua da Esperança, 45 – São Paulo/SP")

    /** Perfis oferecidos em "Preencher com perfil de demonstração" (RF04-CA8). */
    val perfisDemonstracao: List<Perfil> = listOf(padaria, mercado, restaurante, supermercado, mercearia, ongMesaCheia)

    private data class Item(
        val nome: String,
        val categoria: Categoria,
        val quantidade: Double,
        val unidade: Unidade,
        val loja: Perfil,
        val diasValidade: Long,
        val diaRetirada: Long,
        val horaInicio: Int,
        val horaFim: Int,
        val fotoId: String? = null,
    )

    private val disponiveis = listOf(
        Item("Arroz branco tipo 1 – 5 kg", MERCEARIA, 10.0, PACOTE, mercado, 6, 2, 9, 17, "1586201375761-83865001e31c"),
        Item("Feijão carioca 1 kg", MERCEARIA, 15.0, PACOTE, mercearia, 5, 1, 8, 12),
        Item("Farinha de trigo 1 kg", MERCEARIA, 20.0, PACOTE, mercearia, 4, 2, 14, 18, "1610725664285-7c57e6eeac3f"),
        Item("Macarrão espaguete 500 g", MERCEARIA, 30.0, PACOTE, mercado, 7, 4, 10, 16, "1551462147-ff29053bfc14"),
        Item("Café torrado e moído 500 g", MERCEARIA, 10.0, PACOTE, mercado, 4, 3, 8, 11, "1559056199-641a0ac8b55e"),
        Item("Biscoito de chocolate 130 g", MERCEARIA, 25.0, PACOTE, supermercado, 3, 2, 10, 14, "1558961363-fa8fdf82db35"),
        Item("Kit enlatados: milho e ervilha", MERCEARIA, 24.0, Unidade.UNIDADE, supermercado, 4, 1, 10, 15, "1584263347416-85a696b4eda7"),
        Item("Sardinha em lata 125 g", MERCEARIA, 40.0, Unidade.UNIDADE, mercado, 7, 5, 14, 18),
        Item("Pão de forma tradicional", PAES, 12.0, PACOTE, padaria, 2, 1, 16, 19),
        Item("Pão de queijo", PAES, 20.0, Unidade.UNIDADE, padaria, 2, 1, 19, 21),
        Item("Bolo de fubá", PAES, 3.0, Unidade.UNIDADE, padaria, 3, 2, 15, 18),
        Item("Banana prata", HORTIFRUTI, 8.5, KG, supermercado, 4, 2, 14, 18),
        Item("Iogurte natural 170 g", LATICINIOS, 24.0, Unidade.UNIDADE, supermercado, 3, 1, 9, 13),
    )

    private val marmita = Item("Marmita de arroz, feijão e frango", REFEICOES, 15.0, PORCAO, restaurante, 1, 1, 11, 14)

    /** Anúncios disponíveis, com `criadoEm` escalonado para a ordenação ficar estável. */
    fun alimentosDisponiveis(agora: LocalDateTime): List<AlimentoEntity> =
        disponiveis.mapIndexed { indice, item ->
            item.paraEntidade(agora, StatusAlimento.DISPONIVEL, criadoEm = agora.minusHours(indice + 2L))
        }

    /** Marmita já reservada pela ONG Mesa Cheia, com o código [CODIGO_DEMO]. */
    fun marmitaReservada(agora: LocalDateTime): AlimentoEntity =
        marmita.paraEntidade(agora, StatusAlimento.RESERVADO, criadoEm = agora.minusHours(1))

    fun reservaDaMarmita(alimentoId: Long, agora: LocalDateTime) = ReservaEntity(
        alimentoId = alimentoId,
        ongCnpj = ongMesaCheia.cnpj,
        ongNome = ongMesaCheia.nome,
        codigoRetirada = CODIGO_DEMO,
        status = StatusReserva.ATIVA,
        criadaEm = agora.minusMinutes(30),
        atualizadaEm = agora.minusMinutes(30),
    )

    /** Pão francês retirado ontem: mostra um item concluído no histórico. */
    fun paoFrancesRetirado(agora: LocalDateTime): AlimentoEntity {
        val ontem = agora.toLocalDate().minusDays(1)
        return AlimentoEntity(
            nome = "Pão francês",
            categoria = PAES,
            quantidade = 40.0,
            unidade = Unidade.UNIDADE,
            dataValidade = agora.toLocalDate(),
            retiradaInicio = ontem.atTime(16, 0),
            retiradaFim = ontem.atTime(19, 0),
            endereco = padaria.endereco,
            fotoUrl = null,
            status = StatusAlimento.RETIRADO,
            estabelecimentoCnpj = padaria.cnpj,
            estabelecimentoNome = padaria.nome,
            declaracaoProprio = true,
            criadoEm = ontem.atTime(10, 0),
            atualizadoEm = ontem.atTime(17, 30),
        )
    }

    fun reservaDoPaoFrances(alimentoId: Long, agora: LocalDateTime): ReservaEntity {
        val ontem = agora.toLocalDate().minusDays(1)
        return ReservaEntity(
            alimentoId = alimentoId,
            ongCnpj = ongMesaCheia.cnpj,
            ongNome = ongMesaCheia.nome,
            codigoRetirada = CODIGO_HISTORICO,
            status = StatusReserva.CONCLUIDA,
            criadaEm = ontem.atTime(12, 0),
            atualizadaEm = ontem.atTime(17, 30),
        )
    }

    private fun Item.paraEntidade(agora: LocalDateTime, status: StatusAlimento, criadoEm: LocalDateTime): AlimentoEntity {
        val hoje = agora.toLocalDate()
        val diaRetirada = hoje.plusDays(diaRetirada)
        return AlimentoEntity(
            nome = nome,
            categoria = categoria,
            quantidade = quantidade,
            unidade = unidade,
            dataValidade = hoje.plusDays(diasValidade),
            retiradaInicio = diaRetirada.atTime(LocalTime.of(horaInicio, 0)),
            retiradaFim = diaRetirada.atTime(LocalTime.of(horaFim, 0)),
            endereco = loja.endereco,
            fotoUrl = fotoId?.let { "https://images.unsplash.com/photo-$it?w=800" },
            status = status,
            estabelecimentoCnpj = loja.cnpj,
            estabelecimentoNome = loja.nome,
            declaracaoProprio = true,
            criadoEm = criadoEm,
            atualizadoEm = criadoEm,
        )
    }
}
