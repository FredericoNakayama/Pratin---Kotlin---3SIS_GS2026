package br.com.fiap.pratin.domain.model

/** Tipo de perfil escolhido pelo usuário. */
enum class TipoPerfil { ESTABELECIMENTO, ONG }

/** Ciclo de vida de um anúncio (spec/02-requisitos.md §3). */
enum class StatusAlimento {
    DISPONIVEL, RESERVADO, RETIRADO, CANCELADO, EXPIRADO;

    /** Ainda depende de uma ação (reservar ou retirar). */
    val pendente: Boolean get() = this == DISPONIVEL || this == RESERVADO
}

/** Ciclo de vida de uma reserva (spec/02-requisitos.md §3). */
enum class StatusReserva { ATIVA, CONCLUIDA, CANCELADA, EXPIRADA }

/** Categorias de alimento (RN13). Rótulos e ícones ficam na camada de UI. */
enum class Categoria { PAES, HORTIFRUTI, LATICINIOS, CARNES, MERCEARIA, REFEICOES, BEBIDAS, OUTROS }

/** Unidades de medida de um lote (RN13). */
enum class Unidade { UNIDADE, KG, LITRO, PACOTE, CAIXA, PORCAO }
