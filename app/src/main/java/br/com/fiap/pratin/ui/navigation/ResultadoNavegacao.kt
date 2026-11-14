package br.com.fiap.pratin.ui.navigation

import androidx.annotation.StringRes
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

/** Chave usada para uma tela deixar uma mensagem para a anterior ao voltar. */
private const val CHAVE_MENSAGEM = "mensagem_resultado"

/** Volta para a tela anterior entregando [mensagem] (mostrada lá em um Snackbar). */
fun NavHostController.voltarComMensagem(@StringRes mensagem: Int) {
    previousBackStackEntry?.savedStateHandle?.set(CHAVE_MENSAGEM, mensagem)
    popBackStack()
}

/** Mensagem pendente desta entrada da pilha, se houver. */
fun NavBackStackEntry.mensagemResultado() = savedStateHandle.getStateFlow<Int?>(CHAVE_MENSAGEM, null)

fun NavBackStackEntry.limparMensagemResultado() {
    savedStateHandle[CHAVE_MENSAGEM] = null
}
