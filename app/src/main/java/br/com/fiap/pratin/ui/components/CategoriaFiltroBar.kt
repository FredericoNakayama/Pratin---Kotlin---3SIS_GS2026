package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.icone
import br.com.fiap.pratin.ui.util.rotuloRes

/** Chips do Início: Favoritos (liga/desliga), Todos e as categorias (seleção única) — RF08. */
@Composable
fun CategoriaFiltroBar(
    categoriaSelecionada: Categoria?,
    somenteFavoritos: Boolean,
    onCategoriaChange: (Categoria?) -> Unit,
    onFavoritosChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val cores = FilterChipDefaults.filterChipColors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
        selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = Dimens.margemTela),
        horizontalArrangement = Arrangement.spacedBy(Dimens.sm),
    ) {
        item {
            FilterChip(
                colors = cores,
                selected = somenteFavoritos,
                onClick = { onFavoritosChange(!somenteFavoritos) },
                label = { Text(stringResource(R.string.filtro_favoritos)) },
                leadingIcon = {
                    Icon(
                        if (somenteFavoritos) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize),
                    )
                },
            )
        }
        item {
            FilterChip(
                colors = cores,
                selected = categoriaSelecionada == null,
                onClick = { onCategoriaChange(null) },
                label = { Text(stringResource(R.string.filtro_todos)) },
                leadingIcon = if (categoriaSelecionada == null) {
                    { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize)) }
                } else {
                    null
                },
            )
        }
        items(Categoria.entries, key = { it.name }) { categoria ->
            FilterChip(
                colors = cores,
                selected = categoriaSelecionada == categoria,
                onClick = { onCategoriaChange(categoria) },
                label = { Text(stringResource(categoria.rotuloRes())) },
                leadingIcon = {
                    Icon(categoria.icone(), contentDescription = null, modifier = Modifier.size(FilterChipDefaults.IconSize))
                },
            )
        }
    }
}
