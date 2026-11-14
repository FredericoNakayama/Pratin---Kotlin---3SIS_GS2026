package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.model.AnuncioDetalhado
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.domain.model.StatusReserva
import br.com.fiap.pratin.ui.theme.BotaoShape
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.textoQuantidade

/** Reserva da ONG (RF15): código quando ativa e "Cancelar reserva" quando permitido (RF16). */
@Composable
fun ReservaCard(
    detalhe: ReservaDetalhada,
    podeCancelar: Boolean,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alimento = detalhe.alimento
    val reserva = detalhe.reserva
    CardAtividade(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                alimento.nome,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(Dimens.sm))
            StatusChip(reserva.status)
        }
        if (reserva.status == StatusReserva.ATIVA) {
            CodigoRetiradaBox(codigo = reserva.codigoRetirada, compacto = true)
        }
        InfoRow(Icons.Filled.Inventory2, stringResource(R.string.detalhes_quantidade), textoQuantidade(alimento.quantidade, alimento.unidade))
        InfoRow(Icons.Filled.Schedule, stringResource(R.string.detalhes_janela), Formatadores.janela(alimento.retiradaInicio, alimento.retiradaFim))
        InfoRow(Icons.Filled.Storefront, stringResource(R.string.detalhes_estabelecimento), alimento.estabelecimentoNome)
        InfoRow(Icons.Filled.LocationOn, stringResource(R.string.detalhes_endereco), alimento.endereco)
        if (podeCancelar) {
            OutlinedButton(onClick = onCancelar, shape = BotaoShape, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.acao_cancelar_reserva))
            }
        }
    }
}

/** Anúncio do estabelecimento (RF17): cancelar (RF18) ou confirmar a retirada (RF19). */
@Composable
fun AnuncioCard(
    anuncio: AnuncioDetalhado,
    podeCancelar: Boolean,
    podeConfirmarRetirada: Boolean,
    onCancelar: () -> Unit,
    onConfirmarRetirada: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alimento = anuncio.alimento
    CardAtividade(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlimentoImagem(
                fotoUrl = alimento.fotoUrl,
                categoria = alimento.categoria,
                descricao = alimento.nome,
                tamanhoIcone = 24.dp,
                modifier = Modifier.size(48.dp),
            )
            Spacer(Modifier.size(Dimens.md))
            Text(
                alimento.nome,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(Dimens.sm))
            StatusChip(alimento.status)
        }
        InfoRow(Icons.Filled.Inventory2, stringResource(R.string.detalhes_quantidade), textoQuantidade(alimento.quantidade, alimento.unidade))
        InfoRow(Icons.Filled.Schedule, stringResource(R.string.detalhes_janela), Formatadores.janela(alimento.retiradaInicio, alimento.retiradaFim))
        anuncio.reservaAtiva?.let { reserva ->
            InfoRow(Icons.Filled.VolunteerActivism, stringResource(R.string.reservado_por_rotulo), reserva.ongNome)
        }
        when {
            podeConfirmarRetirada -> Button(
                onClick = onConfirmarRetirada,
                shape = BotaoShape,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(Dimens.sm))
                Text(stringResource(R.string.acao_confirmar_retirada))
            }
            podeCancelar -> OutlinedButton(onClick = onCancelar, shape = BotaoShape, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.acao_cancelar_anuncio))
            }
        }
    }
}

@Composable
private fun CardAtividade(modifier: Modifier, conteudo: @Composable () -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) { conteudo() }
    }
}
