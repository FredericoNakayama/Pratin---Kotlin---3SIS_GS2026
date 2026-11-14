package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.rules.CodigoGenerator
import br.com.fiap.pratin.ui.theme.CodigoRetiradaStyle
import br.com.fiap.pratin.ui.theme.Dimens
import androidx.compose.ui.unit.sp

/**
 * Estabelecimento digita o código apresentado pela ONG (RF19). O erro aparece dentro do diálogo,
 * que continua aberto; "Confirmar" só habilita com 6 caracteres.
 */
@Composable
fun ConfirmarRetiradaDialog(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    erro: String?,
    processando: Boolean,
    onConfirmar: () -> Unit,
    onDispensar: () -> Unit,
) {
    val completo = codigo.length == CodigoGenerator.TAMANHO
    AlertDialog(
        onDismissRequest = onDispensar,
        title = { Text(stringResource(R.string.confirmar_retirada_titulo)) },
        text = {
            Column {
                Text(stringResource(R.string.confirmar_retirada_mensagem), style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = codigo,
                    onValueChange = onCodigoChange,
                    label = { Text(stringResource(R.string.campo_codigo)) },
                    singleLine = true,
                    isError = erro != null,
                    supportingText = {
                        Text(erro ?: if (codigo.isNotEmpty() && !completo) stringResource(R.string.erro_codigo_tamanho) else "")
                    },
                    textStyle = CodigoRetiradaStyle.copy(fontSize = 24.sp, letterSpacing = 6.sp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        imeAction = ImeAction.Done,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.md),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirmar, enabled = completo && !processando) {
                Text(stringResource(R.string.acao_confirmar))
            }
        },
        dismissButton = { TextButton(onClick = onDispensar) { Text(stringResource(R.string.acao_cancelar)) } },
    )
}
