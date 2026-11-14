package br.com.fiap.pratin.ui.screens.confirmacao

import android.content.ClipData
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.ui.components.CodigoRetiradaBox
import br.com.fiap.pratin.ui.components.InfoRow
import br.com.fiap.pratin.ui.components.LoadingState
import br.com.fiap.pratin.ui.components.PrimaryButton
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.textoQuantidade
import kotlinx.coroutines.launch

/**
 * Confirmação da reserva (T08). Sem seta de voltar: "voltar" leva ao Início, e os dois botões
 * removem esta tela da pilha (RF14-CA3).
 */
@Composable
fun ConfirmacaoRoute(
    onVerAtividades: () -> Unit,
    onVoltarInicio: () -> Unit,
    viewModel: ConfirmacaoViewModel = viewModel(factory = ConfirmacaoViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val clipboard = LocalClipboard.current
    val scope = rememberCoroutineScope()
    val textoCopiado = stringResource(R.string.msg_codigo_copiado)

    BackHandler(onBack = onVoltarInicio)

    // Sem TopAppBar: o título já aparece em destaque no conteúdo.
    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        val detalhe = state.detalhe
        if (state.carregando || detalhe == null) {
            LoadingState(Modifier.padding(padding))
        } else {
            ConfirmacaoConteudo(
                detalhe = detalhe,
                onCopiar = {
                    scope.launch {
                        clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("codigo", detalhe.reserva.codigoRetirada)))
                        snackbar.showSnackbar(textoCopiado)
                    }
                },
                onVerAtividades = onVerAtividades,
                onVoltarInicio = onVoltarInicio,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
fun ConfirmacaoConteudo(
    detalhe: ReservaDetalhada,
    onCopiar: () -> Unit,
    onVerAtividades: () -> Unit,
    onVoltarInicio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val alimento = detalhe.alimento
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.margemTela),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.lg),
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp),
        )
        Text(stringResource(R.string.confirmacao_titulo), style = MaterialTheme.typography.headlineSmall)
        CodigoRetiradaBox(codigo = detalhe.reserva.codigoRetirada, onCopiar = onCopiar)
        Text(
            stringResource(R.string.confirmacao_aviso),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                InfoRow(Icons.Filled.Inventory2, alimento.nome, textoQuantidade(alimento.quantidade, alimento.unidade))
                InfoRow(Icons.Filled.Storefront, stringResource(R.string.detalhes_estabelecimento), alimento.estabelecimentoNome)
                InfoRow(Icons.Filled.LocationOn, stringResource(R.string.detalhes_endereco), alimento.endereco)
                InfoRow(
                    Icons.Filled.Schedule,
                    stringResource(R.string.detalhes_janela),
                    Formatadores.janela(alimento.retiradaInicio, alimento.retiradaFim),
                )
            }
        }
        PrimaryButton(
            texto = stringResource(R.string.acao_ver_atividades),
            onClick = onVerAtividades,
            icone = Icons.AutoMirrored.Filled.ReceiptLong,
        )
        TextButton(onClick = onVoltarInicio) { Text(stringResource(R.string.acao_voltar_inicio)) }
    }
}
