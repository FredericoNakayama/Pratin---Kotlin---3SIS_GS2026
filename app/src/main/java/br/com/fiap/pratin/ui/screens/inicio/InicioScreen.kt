package br.com.fiap.pratin.ui.screens.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.outlined.NoFood
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.ui.components.AlimentoCard
import br.com.fiap.pratin.ui.components.CategoriaFiltroBar
import br.com.fiap.pratin.ui.components.EmptyState
import br.com.fiap.pratin.ui.components.ErrorState
import br.com.fiap.pratin.ui.components.LoadingState
import br.com.fiap.pratin.ui.components.PerfilNecessarioDialog
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme
import br.com.fiap.pratin.ui.util.asString

@Composable
fun InicioRoute(
    onAbrirDetalhes: (Long) -> Unit,
    onAnunciar: () -> Unit,
    onConfigurarPerfil: () -> Unit,
    onAbrirSobre: () -> Unit,
    viewModel: InicioViewModel = viewModel(factory = InicioViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = state.mensagem?.asString()

    // Ao voltar para a tela, expira o que venceu (RF10-CA2).
    LifecycleResumeEffect(Unit) {
        viewModel.atualizar()
        onPauseOrDispose { }
    }
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            viewModel.mensagemExibida()
        }
    }
    if (state.mostrarPerfilNecessario) {
        PerfilNecessarioDialog(
            onConfigurar = {
                viewModel.dispensarPerfilNecessario()
                onConfigurarPerfil()
            },
            onDispensar = viewModel::dispensarPerfilNecessario,
        )
    }

    InicioScreen(
        state = state,
        snackbar = snackbar,
        acoes = InicioAcoes(
            onBuscaChange = viewModel::onBuscaChange,
            onCategoriaChange = viewModel::onCategoriaChange,
            onFavoritosChange = viewModel::onFavoritosChange,
            onAlternarFavorito = viewModel::alternarFavorito,
            onAbrirDetalhes = onAbrirDetalhes,
            onAtualizar = { viewModel.atualizar(comIndicador = true) },
            onTentarNovamente = viewModel::tentarNovamente,
            onAnunciar = { if (viewModel.onAnunciar()) onAnunciar() },
            onAbrirSobre = onAbrirSobre,
        ),
    )
}

data class InicioAcoes(
    val onBuscaChange: (String) -> Unit = {},
    val onCategoriaChange: (Categoria?) -> Unit = {},
    val onFavoritosChange: (Boolean) -> Unit = {},
    val onAlternarFavorito: (Long) -> Unit = {},
    val onAbrirDetalhes: (Long) -> Unit = {},
    val onAtualizar: () -> Unit = {},
    val onTentarNovamente: () -> Unit = {},
    val onAnunciar: () -> Unit = {},
    val onAbrirSobre: () -> Unit = {},
)

/** Início (T04): saudação, busca, filtros e a lista de alimentos disponíveis. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    state: InicioUiState,
    acoes: InicioAcoes,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(R.drawable.logo_pratin),
                            contentDescription = null,
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(Modifier.size(Dimens.sm))
                        Text(stringResource(R.string.app_name))
                    }
                },
                actions = {
                    IconButton(onClick = acoes.onAbrirSobre) {
                        Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.cd_sobre))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        floatingActionButton = {
            if (state.podeAnunciar) {
                ExtendedFloatingActionButton(
                    onClick = acoes.onAnunciar,
                    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                    text = { Text(stringResource(R.string.acao_anunciar)) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Cabecalho(nome = state.perfil?.nome)
            CampoBusca(state.busca, acoes.onBuscaChange)
            CategoriaFiltroBar(
                categoriaSelecionada = state.categoria,
                somenteFavoritos = state.somenteFavoritos,
                onCategoriaChange = acoes.onCategoriaChange,
                onFavoritosChange = acoes.onFavoritosChange,
                modifier = Modifier.padding(vertical = Dimens.sm),
            )
            PullToRefreshBox(
                isRefreshing = state.atualizando,
                onRefresh = acoes.onAtualizar,
                modifier = Modifier.fillMaxSize(),
            ) {
                when {
                    state.carregando -> LoadingState()
                    state.erro != null -> ErrorState(state.erro.asString(), acoes.onTentarNovamente)
                    state.alimentos.isEmpty() -> EmptyState(
                        icone = when {
                            state.somenteFavoritos -> Icons.Filled.FavoriteBorder
                            state.totalDisponiveis > 0 -> Icons.Filled.SearchOff
                            else -> Icons.Outlined.NoFood
                        },
                        mensagem = stringResource(state.mensagemVazio),
                    )
                    else -> ListaAlimentos(state, acoes)
                }
            }
        }
    }
}

@Composable
private fun Cabecalho(nome: String?) {
    Column(Modifier.padding(horizontal = Dimens.margemTela)) {
        Text(
            text = if (nome != null) stringResource(R.string.inicio_saudacao_nome, nome) else stringResource(R.string.inicio_saudacao),
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = stringResource(R.string.inicio_subtitulo),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun CampoBusca(busca: String, onBuscaChange: (String) -> Unit) {
    OutlinedTextField(
        value = busca,
        onValueChange = onBuscaChange,
        placeholder = { Text(stringResource(R.string.inicio_buscar)) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (busca.isNotEmpty()) {
                IconButton(onClick = { onBuscaChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = stringResource(R.string.cd_limpar_busca))
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.margemTela, vertical = Dimens.sm),
    )
}

/** Lista em uma coluna; grade de 2 colunas a partir de 600 dp (RF06-CA5). */
@Composable
private fun ListaAlimentos(state: InicioUiState, acoes: InicioAcoes) {
    val espacamento = PaddingValues(start = Dimens.margemTela, end = Dimens.margemTela, top = Dimens.xs, bottom = 96.dp)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        if (maxWidth >= Dimens.larguraGrade) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = espacamento,
                horizontalArrangement = Arrangement.spacedBy(Dimens.md),
                verticalArrangement = Arrangement.spacedBy(Dimens.md),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.alimentos, key = { it.id }) { CardDoAlimento(it, state, acoes) }
            }
        } else {
            LazyColumn(
                contentPadding = espacamento,
                verticalArrangement = Arrangement.spacedBy(Dimens.md),
                modifier = Modifier.fillMaxSize(),
            ) {
                items(state.alimentos, key = { it.id }) { CardDoAlimento(it, state, acoes) }
            }
        }
    }
}

@Composable
private fun CardDoAlimento(alimento: Alimento, state: InicioUiState, acoes: InicioAcoes) {
    AlimentoCard(
        alimento = alimento,
        hoje = state.hoje,
        onClick = { acoes.onAbrirDetalhes(alimento.id) },
        onAlternarFavorito = { acoes.onAlternarFavorito(alimento.id) },
    )
}

@Preview(showBackground = true)
@Composable
private fun InicioVazioPreview() {
    PratinTheme {
        InicioScreen(state = InicioUiState(carregando = false, perfil = SeedData.padaria), acoes = InicioAcoes())
    }
}
