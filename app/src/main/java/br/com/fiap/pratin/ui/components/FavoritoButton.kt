package br.com.fiap.pratin.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.theme.PratinColors

/** Coração de favoritar (RF09). Área de toque de 48 dp. */
@Composable
fun FavoritoButton(favorito: Boolean, onAlternar: () -> Unit, modifier: Modifier = Modifier) {
    IconToggleButton(checked = favorito, onCheckedChange = { onAlternar() }, modifier = modifier) {
        Icon(
            imageVector = if (favorito) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(if (favorito) R.string.cd_desfavoritar else R.string.cd_favoritar),
            tint = if (favorito) PratinColors.favorito else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
