package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.ui.theme.BotaoShape
import br.com.fiap.pratin.ui.theme.Dimens

/** Botão principal de largura total; mostra progresso e fica desabilitado enquanto [carregando]. */
@Composable
fun PrimaryButton(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    carregando: Boolean = false,
    habilitado: Boolean = true,
    icone: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        enabled = habilitado && !carregando,
        shape = BotaoShape,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp),
    ) {
        if (carregando) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = LocalContentColor.current,
            )
            Spacer(Modifier.size(Dimens.sm))
        } else if (icone != null) {
            Icon(icone, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.size(Dimens.sm))
        }
        Text(texto)
    }
}
