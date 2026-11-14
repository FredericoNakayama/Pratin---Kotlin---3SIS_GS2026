package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.format.Vencimento
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.rotuloRes
import br.com.fiap.pratin.ui.util.texto
import br.com.fiap.pratin.ui.util.textoQuantidade
import java.time.LocalDate

/** Card de um alimento disponível no Início (RF06-CA2). */
@Composable
fun AlimentoCard(
    alimento: Alimento,
    hoje: LocalDate,
    onClick: () -> Unit,
    onAlternarFavorito: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(Modifier.padding(Dimens.md), verticalAlignment = Alignment.Top) {
            AlimentoImagem(
                fotoUrl = alimento.fotoUrl,
                categoria = alimento.categoria,
                descricao = alimento.nome,
                modifier = Modifier.size(88.dp),
            )
            Spacer(Modifier.size(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(
                    text = alimento.nome,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${textoQuantidade(alimento.quantidade, alimento.unidade)} · " +
                        stringResource(alimento.categoria.rotuloRes()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                VencimentoTexto(Formatadores.vencimento(alimento.dataValidade, hoje))
                Text(
                    text = stringResource(R.string.janela_retirada, Formatadores.janela(alimento.retiradaInicio, alimento.retiradaFim)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = alimento.estabelecimentoNome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            FavoritoButton(favorito = alimento.favorito, onAlternar = onAlternarFavorito)
        }
    }
}

/** "Vence hoje/amanhã" em laranja; os demais prazos em cinza (spec/04-ui.md T04). */
@Composable
fun VencimentoTexto(vencimento: Vencimento, modifier: Modifier = Modifier) {
    val urgente = vencimento == Vencimento.Hoje || vencimento == Vencimento.Amanha || vencimento == Vencimento.Vencido
    val cor = if (urgente) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Event, contentDescription = null, tint = cor, modifier = Modifier.size(16.dp))
        Spacer(Modifier.size(Dimens.xs))
        Text(
            text = vencimento.texto(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (urgente) FontWeight.SemiBold else FontWeight.Normal,
            color = cor,
        )
    }
}
