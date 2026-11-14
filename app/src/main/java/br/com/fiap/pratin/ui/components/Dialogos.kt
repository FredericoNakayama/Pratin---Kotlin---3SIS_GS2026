package br.com.fiap.pratin.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fiap.pratin.R

/** Quem não tem perfil tenta anunciar ou reservar (RF21-CA1). */
@Composable
fun PerfilNecessarioDialog(onConfigurar: () -> Unit, onDispensar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDispensar,
        icon = { Icon(Icons.Filled.Person, contentDescription = null) },
        title = { Text(stringResource(R.string.perfil_necessario_titulo)) },
        text = { Text(stringResource(R.string.msg_perfil_necessario)) },
        confirmButton = {
            TextButton(onClick = onConfigurar) { Text(stringResource(R.string.acao_configurar_perfil)) }
        },
        dismissButton = {
            TextButton(onClick = onDispensar) { Text(stringResource(R.string.acao_agora_nao)) }
        },
    )
}

/** Confirmação genérica antes de ações que mudam o status (reservar, cancelar). */
@Composable
fun ConfirmacaoDialog(
    titulo: String,
    mensagem: String,
    textoConfirmar: String,
    onConfirmar: () -> Unit,
    onDispensar: () -> Unit,
    textoDispensar: String = stringResource(R.string.acao_cancelar),
) {
    AlertDialog(
        onDismissRequest = onDispensar,
        title = { Text(titulo) },
        text = { Text(mensagem) },
        confirmButton = { TextButton(onClick = onConfirmar) { Text(textoConfirmar) } },
        dismissButton = { TextButton(onClick = onDispensar) { Text(textoDispensar) } },
    )
}
