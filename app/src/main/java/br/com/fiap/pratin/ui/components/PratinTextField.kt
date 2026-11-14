package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Campo de texto padrão dos formulários: rótulo, erro embaixo do campo e teclado configurável.
 * [erro] nulo = campo válido.
 */
@Composable
fun PratinTextField(
    valor: String,
    onValorChange: (String) -> Unit,
    rotulo: String,
    modifier: Modifier = Modifier,
    erro: String? = null,
    apoio: String? = null,
    tipoTeclado: KeyboardType = KeyboardType.Text,
    capitalizacao: KeyboardCapitalization = KeyboardCapitalization.Sentences,
    acaoIme: ImeAction = ImeAction.Next,
    transformacao: VisualTransformation = VisualTransformation.None,
    linhaUnica: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        label = { Text(rotulo) },
        modifier = modifier.fillMaxWidth(),
        isError = erro != null,
        supportingText = (erro ?: apoio)?.let { texto -> { Text(texto) } },
        singleLine = linhaUnica,
        keyboardOptions = KeyboardOptions(
            keyboardType = tipoTeclado,
            capitalization = capitalizacao,
            imeAction = acaoIme,
        ),
        visualTransformation = transformacao,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    )
}
