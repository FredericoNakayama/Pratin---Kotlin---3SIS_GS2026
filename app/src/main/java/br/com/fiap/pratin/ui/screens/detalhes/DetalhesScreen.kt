package br.com.fiap.pratin.ui.screens.detalhes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.ui.components.AlimentoImagem
import br.com.fiap.pratin.ui.components.ConfirmacaoDialog
import br.com.fiap.pratin.ui.components.EmptyState
import br.com.fiap.pratin.ui.components.FavoritoButton
import br.com.fiap.pratin.ui.components.InfoRow
import br.com.fiap.pratin.ui.components.LoadingState
import br.com.fiap.pratin.ui.components.PerfilNecessarioDialog
import br.com.fiap.pratin.ui.components.PratinTopAppBar
import br.com.fiap.pratin.ui.components.PrimaryButton
import br.com.fiap.pratin.ui.components.StatusChip
import br.com.fiap.pratin.ui.components.VencimentoTexto
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.asString
import br.com.fiap.pratin.ui.util.rotuloRes
import br.com.fiap.pratin.ui.util.textoQuantidade

@Composable
fun DetalhesRoute(
    onVoltar: () -> Unit,
    onReservado: (Long) -> Unit,
    onConfigurarPerfil: () -> Unit,
    viewModel: DetalhesViewModel = viewModel(factory = DetalhesViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = state.mensagem?.asString()

    LaunchedEffect(state.reservaCriadaId) {
        state.reservaCriadaId?.let {
            viewModel.navegouParaConfirmacao()
            onReservado(it)
        }
    }
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            viewModel.mensagemExibida()
        }
    }
    if (state.confirmarReserva) {
        ConfirmacaoDialog(
            titulo = stringResource(R.string.reservar_titulo),
            mensagem = stringResource(R.string.reservar_mensagem),
            textoConfirmar = stringResource(R.string.acao_reservar),
            onConfirmar = viewModel::confirmarReserva,
            onDispensar = viewModel::dispensarDialogos,
        )
    }
    if (state.mostrarPerfilNecessario) {
        PerfilNecessarioDialog(
            onConfigurar = {
                viewModel.dispensarDialogos()
                onConfigurarPerfil()
            },
            onDispensar = viewModel::dispensarDialogos,
        )
    }

    DetalhesScreen(
        state = state,
        snackbar = snackbar,
        onVoltar = onVoltar,
        onAlternarFavorito = viewModel::alternarFavorito,
        onReservar = viewModel::onReservar,
    )
}

/** Detalhes do alimento (T05). */
@Composable
fun DetalhesScreen(
    state: DetalhesUiState,
    onVoltar: () -> Unit,
    onAlternarFavorito: () -> Unit,
    onReservar: () -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    val alimento = state.alimento
    Scaffold(
        modifier = modifier,
        topBar = {
            PratinTopAppBar(
                titulo = alimento?.nome.orEmpty(),
                onVoltar = onVoltar,
                acoes = {
                    if (alimento != null) FavoritoButton(alimento.favorito, onAlternarFavorito)
                },
            )
        },
        bottomBar = { if (alimento != null) Rodape(state, onReservar) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            state.carregando -> LoadingState(Modifier.padding(padding))
            alimento == null -> EmptyState(
                icone = Icons.Outlined.NoFood,
                mensagem = stringResource(R.string.erro_alimento_nao_encontrado),
                textoAcao = stringResource(R.string.acao_voltar_inicio),
                onAcao = onVoltar,
                modifier = Modifier.padding(padding),
            )
            else -> Conteudo(alimento, state, Modifier.padding(padding))
        }
    }
}

@Composable
private fun Conteudo(alimento: Alimento, state: DetalhesUiState, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.margemTela),
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        AlimentoImagem(
            fotoUrl = alimento.fotoUrl,
            categoria = alimento.categoria,
            descricao = alimento.nome,
            tamanhoIcone = 72.dp,
            forma = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusChip(alimento.status)
            Spacer(Modifier.size(Dimens.sm))
            VencimentoTexto(Formatadores.vencimento(alimento.dataValidade, state.hoje))
        }
        Text(alimento.nome, style = MaterialTheme.typography.headlineSmall)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                InfoRow(Icons.Filled.Inventory2, stringResource(R.string.detalhes_quantidade), textoQuantidade(alimento.quantidade, alimento.unidade))
                InfoRow(Icons.Filled.Category, stringResource(R.string.detalhes_categoria), stringResource(alimento.categoria.rotuloRes()))
                InfoRow(Icons.Filled.Event, stringResource(R.string.detalhes_validade), Formatadores.data(alimento.dataValidade))
                InfoRow(
                    Icons.Filled.Schedule,
                    stringResource(R.string.detalhes_janela),
                    Formatadores.janela(alimento.retiradaInicio, alimento.retiradaFim),
                )
                HorizontalDivider()
                InfoRow(Icons.Filled.Storefront, stringResource(R.string.detalhes_estabelecimento), alimento.estabelecimentoNome)
                InfoRow(Icons.Filled.LocationOn, stringResource(R.string.detalhes_endereco), alimento.endereco)
            }
        }

        AssistChip(
            onClick = {},
            label = { Text(stringResource(R.string.detalhes_doacao_gratuita)) },
            leadingIcon = { Icon(Icons.Filled.VolunteerActivism, contentDescription = null, modifier = Modifier.size(AssistChipDefaults.IconSize)) },
        )
        if (alimento.declaracaoProprio) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.size(Dimens.sm))
                Text(
                    stringResource(R.string.detalhes_declaracao),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Ação por perfil (RF11-CA4): ONG ou sem perfil → Reservar; estabelecimento → aviso. */
@Composable
private fun Rodape(state: DetalhesUiState, onReservar: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 4.dp) {
        Column(
            Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(Dimens.margemTela),
        ) {
            when (state.acao) {
                AcaoDetalhes.RESERVAR -> PrimaryButton(
                    texto = stringResource(R.string.acao_reservar),
                    onClick = onReservar,
                    carregando = state.reservando,
                    icone = Icons.Filled.VolunteerActivism,
                )
                AcaoDetalhes.APENAS_ONGS -> Aviso(stringResource(R.string.erro_apenas_ongs))
                AcaoDetalhes.INDISPONIVEL -> Aviso(stringResource(R.string.detalhes_indisponivel))
            }
        }
    }
}

@Composable
private fun Aviso(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.sm),
    )
}
