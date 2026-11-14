package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.ui.util.icone
import coil3.compose.AsyncImage

/**
 * Foto do anúncio por URL (Coil) sobre o ícone da categoria. Sem URL, sem internet ou com erro,
 * o ícone continua visível (ADR-06).
 */
@Composable
fun AlimentoImagem(
    fotoUrl: String?,
    categoria: Categoria,
    descricao: String,
    modifier: Modifier = Modifier,
    tamanhoIcone: Dp = 40.dp,
    forma: Shape = MaterialTheme.shapes.small,
) {
    Box(
        modifier = modifier
            .clip(forma)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = categoria.icone(),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(tamanhoIcone),
        )
        if (!fotoUrl.isNullOrBlank()) {
            AsyncImage(
                model = fotoUrl,
                contentDescription = descricao,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
