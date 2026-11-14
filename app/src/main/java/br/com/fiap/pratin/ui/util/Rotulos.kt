package br.com.fiap.pratin.ui.util

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EggAlt
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SetMeal
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.format.Vencimento
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.domain.model.TipoPerfil
import br.com.fiap.pratin.domain.model.Unidade
import br.com.fiap.pratin.ui.theme.PratinColors

// Rótulos, ícones e cores dos enums do domínio (o domínio não conhece Android, ADR-07).

@StringRes
fun Categoria.rotuloRes(): Int = when (this) {
    Categoria.PAES -> R.string.categoria_paes
    Categoria.HORTIFRUTI -> R.string.categoria_hortifruti
    Categoria.LATICINIOS -> R.string.categoria_laticinios
    Categoria.CARNES -> R.string.categoria_carnes
    Categoria.MERCEARIA -> R.string.categoria_mercearia
    Categoria.REFEICOES -> R.string.categoria_refeicoes
    Categoria.BEBIDAS -> R.string.categoria_bebidas
    Categoria.OUTROS -> R.string.categoria_outros
}

fun Categoria.icone(): ImageVector = when (this) {
    Categoria.PAES -> Icons.Filled.BakeryDining
    Categoria.HORTIFRUTI -> Icons.Filled.Eco
    Categoria.LATICINIOS -> Icons.Filled.EggAlt
    Categoria.CARNES -> Icons.Filled.SetMeal
    Categoria.MERCEARIA -> Icons.Filled.ShoppingBasket
    Categoria.REFEICOES -> Icons.Filled.Restaurant
    Categoria.BEBIDAS -> Icons.Filled.LocalDrink
    Categoria.OUTROS -> Icons.Filled.Fastfood
}

@StringRes
fun TipoPerfil.rotuloRes(): Int = when (this) {
    TipoPerfil.ESTABELECIMENTO -> R.string.tipo_estabelecimento
    TipoPerfil.ONG -> R.string.tipo_ong
}

fun TipoPerfil.icone(): ImageVector = when (this) {
    TipoPerfil.ESTABELECIMENTO -> Icons.Filled.Storefront
    TipoPerfil.ONG -> Icons.Filled.VolunteerActivism
}

@StringRes
fun StatusAlimento.rotuloRes(): Int = when (this) {
    StatusAlimento.DISPONIVEL -> R.string.status_disponivel
    StatusAlimento.RESERVADO -> R.string.status_reservado
    StatusAlimento.RETIRADO -> R.string.status_retirado
    StatusAlimento.CANCELADO -> R.string.status_cancelado
    StatusAlimento.EXPIRADO -> R.string.status_expirado
}

fun StatusAlimento.cor(): Color = when (this) {
    StatusAlimento.DISPONIVEL -> PratinColors.statusDisponivel
    StatusAlimento.RESERVADO -> PratinColors.statusReservado
    StatusAlimento.RETIRADO -> PratinColors.statusRetirado
    StatusAlimento.CANCELADO -> PratinColors.statusCancelado
    StatusAlimento.EXPIRADO -> PratinColors.statusExpirado
}

@StringRes
fun StatusReserva.rotuloRes(): Int = when (this) {
    StatusReserva.ATIVA -> R.string.status_ativa
    StatusReserva.CONCLUIDA -> R.string.status_concluida
    StatusReserva.CANCELADA -> R.string.status_cancelada
    StatusReserva.EXPIRADA -> R.string.status_expirada
}

fun StatusReserva.cor(): Color = when (this) {
    StatusReserva.ATIVA -> PratinColors.statusDisponivel
    StatusReserva.CONCLUIDA -> PratinColors.statusRetirado
    StatusReserva.CANCELADA -> PratinColors.statusCancelado
    StatusReserva.EXPIRADA -> PratinColors.statusExpirado
}

/** Rótulo da unidade no singular quando a quantidade é 1, senão no plural (RN13). */
@StringRes
fun Unidade.rotuloRes(quantidade: Double = 2.0): Int {
    val singular = quantidade == 1.0
    return when (this) {
        Unidade.UNIDADE -> if (singular) R.string.unidade_unidade_um else R.string.unidade_unidade_varios
        Unidade.KG -> if (singular) R.string.unidade_kg_um else R.string.unidade_kg_varios
        Unidade.LITRO -> if (singular) R.string.unidade_litro_um else R.string.unidade_litro_varios
        Unidade.PACOTE -> if (singular) R.string.unidade_pacote_um else R.string.unidade_pacote_varios
        Unidade.CAIXA -> if (singular) R.string.unidade_caixa_um else R.string.unidade_caixa_varios
        Unidade.PORCAO -> if (singular) R.string.unidade_porcao_um else R.string.unidade_porcao_varios
    }
}

/** "15 porções", "8,5 kg", "1 caixa". */
@Composable
fun textoQuantidade(quantidade: Double, unidade: Unidade): String =
    "${Formatadores.quantidade(quantidade)} ${stringResource(unidade.rotuloRes(quantidade))}"

/** "Vence hoje", "Vence amanhã", "Vence em 3 dias" ou "Vencido" (RN14). */
@Composable
fun Vencimento.texto(): String = when (this) {
    Vencimento.Hoje -> stringResource(R.string.vence_hoje)
    Vencimento.Amanha -> stringResource(R.string.vence_amanha)
    is Vencimento.EmDias -> pluralStringResource(R.plurals.vence_em_dias, dias, dias)
    Vencimento.Vencido -> stringResource(R.string.vencido)
}

@StringRes
fun ErroCampo.mensagemRes(): Int = when (this) {
    ErroCampo.NOME_ALIMENTO -> R.string.erro_nome_alimento
    ErroCampo.CATEGORIA -> R.string.erro_categoria
    ErroCampo.QUANTIDADE -> R.string.erro_quantidade
    ErroCampo.UNIDADE -> R.string.erro_unidade
    ErroCampo.VALIDADE_OBRIGATORIA -> R.string.erro_validade_obrigatoria
    ErroCampo.VALIDADE_FORA_DO_PRAZO -> R.string.erro_validade_fora_prazo
    ErroCampo.JANELA_OBRIGATORIA -> R.string.erro_janela_obrigatoria
    ErroCampo.JANELA_NO_PASSADO -> R.string.erro_janela_passado
    ErroCampo.JANELA_CURTA -> R.string.erro_janela_curta
    ErroCampo.JANELA_APOS_VALIDADE -> R.string.erro_janela_apos_validade
    ErroCampo.ENDERECO -> R.string.erro_endereco
    ErroCampo.FOTO_URL -> R.string.erro_foto_url
    ErroCampo.DECLARACAO -> R.string.erro_declaracao
    ErroCampo.TIPO_PERFIL -> R.string.erro_tipo_perfil
    ErroCampo.NOME_PERFIL -> R.string.erro_nome_perfil
    ErroCampo.CNPJ -> R.string.erro_cnpj
}
