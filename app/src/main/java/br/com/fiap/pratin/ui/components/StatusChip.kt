package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.cor
import br.com.fiap.pratin.ui.util.rotuloRes

/** Etiqueta colorida do status de um alimento. */
@Composable
fun StatusChip(status: StatusAlimento, modifier: Modifier = Modifier) {
    Etiqueta(stringResource(status.rotuloRes()), status.cor(), modifier)
}

/** Etiqueta colorida do status de uma reserva. */
@Composable
fun StatusChip(status: StatusReserva, modifier: Modifier = Modifier) {
    Etiqueta(stringResource(status.rotuloRes()), status.cor(), modifier)
}

@Composable
private fun Etiqueta(texto: String, cor: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = cor.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, cor.copy(alpha = 0.6f)),
    ) {
        Text(
            text = texto,
            color = cor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = Dimens.md, vertical = Dimens.xs),
        )
    }
}
