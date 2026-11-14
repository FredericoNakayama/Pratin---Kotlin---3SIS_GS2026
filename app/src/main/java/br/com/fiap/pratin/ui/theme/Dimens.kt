package br.com.fiap.pratin.ui.theme

import androidx.compose.ui.unit.dp

/** Escala de espaçamento de 4 dp (spec/04-ui.md §1). */
object Dimens {
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 24.dp
    val xxl = 32.dp

    /** Margem lateral padrão das telas. */
    val margemTela = 16.dp

    /** Área mínima de toque (acessibilidade). */
    val toqueMinimo = 48.dp

    /** Largura a partir da qual o Início usa grade de 2 colunas. */
    val larguraGrade = 600.dp
}
