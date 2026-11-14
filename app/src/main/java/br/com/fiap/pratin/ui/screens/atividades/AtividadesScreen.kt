package br.com.fiap.pratin.ui.screens.atividades

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.rules.RegrasStatus
import br.com.fiap.pratin.ui.components.AnuncioCard
import br.com.fiap.pratin.ui.components.ConfirmacaoDialog
import br.com.fiap.pratin.ui.components.ConfirmarRetiradaDialog
import br.com.fiap.pratin.ui.components.EmptyState
import br.com.fiap.pratin.ui.components.LoadingState
import br.com.fiap.pratin.ui.components.PratinTopAppBar
import br.com.fiap.pratin.ui.components.ReservaCard
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.util.asString

@Composable
fun AtividadesRoute(
    onConfigurarPerfil: () -> Unit,
    onAnunciar: () -> Unit,
    /** Mensagem deixada por outra tela ao voltar (ex.: "Anúncio publicado!"). */
    mensagemResultado: Int? = null,
    onMensagemResultadoExibida: () -> Unit = {},
    viewModel: AtividadesViewModel = viewModel(factory = AtividadesViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = state.mensagem?.asString()
    val textoResultado = mensagemResultado?.let { stringResource(it) }
    val escopo = rememberCoroutineScope()

    LaunchedEffect(textoResultado) {
        if (textoResultado != null) {
            escopo.launch { snackbar.showSnackbar(textoResultado) }
            onMensagemResultadoExibida()
        }
    }

    // Ao voltar para a tela, expira o que venceu (RF20).
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
    Dialogos(state, viewModel)

    AtividadesScreen(
        state = state,
        snackbar = snackbar,
        onAtualizar = { viewModel.atualizar(comIndicador = true) },
        onAbrirDialogo = viewModel::abrirDialogo,
        onConfigurarPerfil = onConfigurarPerfil,
        onAnunciar = onAnunciar,
    )
}

@Composable
private fun Dialogos(state: AtividadesUiState, viewModel: AtividadesViewModel) {
    when (state.dialogo) {
        is DialogoAtividade.CancelarReserva -> ConfirmacaoDialog(
            titulo = stringResource(R.string.cancelar_reserva_titulo),
            mensagem = stringResource(R.string.cancelar_reserva_mensagem),
            textoConfirmar = stringResource(R.string.acao_cancelar_reserva),
            textoDispensar = stringResource(R.string.acao_manter),
            onConfirmar = viewModel::confirmarDialogo,
            onDispensar = viewModel::fecharDialogo,
        )
        is DialogoAtividade.CancelarAnuncio -> ConfirmacaoDialog(
            titulo = stringResource(R.string.cancelar_anuncio_titulo),
            mensagem = stringResource(R.string.cancelar_anuncio_mensagem),
            textoConfirmar = stringResource(R.string.acao_cancelar_anuncio),
            textoDispensar = stringResource(R.string.acao_manter),
            onConfirmar = viewModel::confirmarDialogo,
            onDispensar = viewModel::fecharDialogo,
        )
        is DialogoAtividade.ConfirmarRetirada -> ConfirmarRetiradaDialog(
            codigo = state.codigoDigitado,
            onCodigoChange = viewModel::onCodigoChange,
            erro = state.erroCodigo?.asString(),
            processando = state.processando,
            onConfirmar = viewModel::confirmarDialogo,
            onDispensar = viewModel::fecharDialogo,
        )
        null -> Unit
    }
}

/** Minhas atividades (T09): reservas da ONG ou anúncios do estabelecimento. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtividadesScreen(
    state: AtividadesUiState,
    onAtualizar: () -> Unit,
    onAbrirDialogo: (DialogoAtividade) -> Unit,
    onConfigurarPerfil: () -> Unit,
    onAnunciar: () -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    val perfil = state.perfil
    val titulo = stringResource(
        when {
            perfil == null -> R.string.atividades_titulo
            perfil.isOng -> R.string.atividades_minhas_reservas
            else -> R.string.atividades_meus_anuncios
        },
    )
    Scaffold(
        modifier = modifier,
        topBar = { PratinTopAppBar(titulo = titulo) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = state.atualizando,
            onRefresh = onAtualizar,
            modifier = Modifier.fillMaxSize().padding(padding),
        ) {
            val espacamento = PaddingValues(Dimens.margemTela)
            when {
                state.carregando -> LoadingState()
                perfil == null -> EmptyState(
                    icone = Icons.Outlined.PersonOutline,
                    mensagem = stringResource(R.string.msg_sem_perfil_atividades),
                    textoAcao = stringResource(R.string.acao_configurar_perfil),
                    onAcao = onConfigurarPerfil,
                )
                perfil.isOng && state.reservas.isEmpty() -> EmptyState(
                    icone = Icons.AutoMirrored.Outlined.ReceiptLong,
                    mensagem = stringResource(R.string.msg_reservas_vazias),
                )
                perfil.isOng -> LazyColumn(
                    contentPadding = espacamento,
                    verticalArrangement = Arrangement.spacedBy(Dimens.md),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.reservas, key = { it.reserva.id }) { detalhe ->
                        ReservaCard(
                            detalhe = detalhe,
                            podeCancelar = RegrasStatus.podeCancelarReserva(detalhe.reserva, detalhe.alimento, state.agora),
                            onCancelar = { onAbrirDialogo(DialogoAtividade.CancelarReserva(detalhe.reserva.id)) },
                        )
                    }
                }
                state.anuncios.isEmpty() -> EmptyState(
                    icone = Icons.Outlined.Campaign,
                    mensagem = stringResource(R.string.msg_anuncios_vazios),
                    textoAcao = stringResource(R.string.acao_anunciar),
                    onAcao = onAnunciar,
                )
                else -> LazyColumn(
                    contentPadding = espacamento,
                    verticalArrangement = Arrangement.spacedBy(Dimens.md),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(state.anuncios, key = { it.alimento.id }) { anuncio ->
                        val id = anuncio.alimento.id
                        AnuncioCard(
                            anuncio = anuncio,
                            podeCancelar = RegrasStatus.podeCancelarAnuncio(anuncio.alimento),
                            podeConfirmarRetirada = RegrasStatus.podeConfirmarRetirada(anuncio.alimento, state.agora),
                            onCancelar = { onAbrirDialogo(DialogoAtividade.CancelarAnuncio(id)) },
                            onConfirmarRetirada = { onAbrirDialogo(DialogoAtividade.ConfirmarRetirada(id)) },
                        )
                    }
                }
            }
        }
    }
}
