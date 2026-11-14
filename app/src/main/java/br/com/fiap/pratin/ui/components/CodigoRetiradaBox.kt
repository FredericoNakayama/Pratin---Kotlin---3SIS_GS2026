package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.theme.CodigoRetiradaStyle
import br.com.fiap.pratin.ui.theme.Dimens

/** Código de retirada em destaque, legível em voz alta (RN07). [compacto] para os cards de Atividades. */
@Composable
fun CodigoRetiradaBox(
    codigo: String,
    modifier: Modifier = Modifier,
    compacto: Boolean = false,
    onCopiar: (() -> Unit)? = null,
) {
    val rotulo = stringResource(R.string.confirmacao_codigo)
    val estilo: TextStyle = if (compacto) CodigoRetiradaStyle.copy(fontSize = 20.sp, letterSpacing = 4.sp) else CodigoRetiradaStyle
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.primaryContainer,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.lg, vertical = if (compacto) Dimens.sm else Dimens.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(rotulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(
                    text = codigo,
                    style = estilo,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    // Leitores de tela soletram o código caractere a caractere.
                    modifier = Modifier.semantics { contentDescription = "$rotulo: ${codigo.toList().joinToString(" ")}" },
                )
            }
            if (onCopiar != null) {
                IconButton(onClick = onCopiar) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.cd_copiar_codigo),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
    }
}
