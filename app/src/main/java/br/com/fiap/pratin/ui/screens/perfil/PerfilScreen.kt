package br.com.fiap.pratin.ui.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.domain.error.CampoPerfil
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.TipoPerfil
import br.com.fiap.pratin.ui.components.CnpjVisualTransformation
import br.com.fiap.pratin.ui.components.InfoRow
import br.com.fiap.pratin.ui.components.PratinTextField
import br.com.fiap.pratin.ui.components.PratinTopAppBar
import br.com.fiap.pratin.ui.components.PrimaryButton
import br.com.fiap.pratin.ui.theme.BotaoShape
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme
import br.com.fiap.pratin.ui.util.asString
import br.com.fiap.pratin.ui.util.icone
import br.com.fiap.pratin.ui.util.mensagemRes
import br.com.fiap.pratin.ui.util.rotuloRes

/**
 * Perfil (T06). Na aba mostra o resumo ou o formulário; em [telaCheia] (aberta pelo diálogo
 * "Perfil necessário") mostra só o formulário e volta à origem depois de salvar (RF04-CA7).
 */
@Composable
fun PerfilRoute(
    onAbrirSobre: () -> Unit,
    onAbrirIntroducao: () -> Unit,
    telaCheia: Boolean = false,
    onVoltar: (() -> Unit)? = null,
    viewModel: PerfilViewModel = viewModel(factory = PerfilViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = state.mensagem?.asString()

    LaunchedEffect(Unit) { if (telaCheia) viewModel.iniciarEdicao() }
    LaunchedEffect(state.salvo) { if (state.salvo && telaCheia) onVoltar?.invoke() }
    LaunchedEffect(state.abrirIntroducao) {
        if (state.abrirIntroducao) {
            viewModel.introducaoAberta()
            onAbrirIntroducao()
        }
    }
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            viewModel.mensagemExibida()
        }
    }

    PerfilScreen(
        state = state,
        snackbar = snackbar,
        onVoltar = onVoltar,
        acoes = PerfilAcoes(
            onTipoChange = viewModel::onTipoChange,
            onNomeChange = viewModel::onNomeChange,
            onCnpjChange = viewModel::onCnpjChange,
            onEnderecoChange = viewModel::onEnderecoChange,
            onPreencherDemonstracao = viewModel::preencherDemonstracao,
            onSalvar = viewModel::salvar,
            onEditar = viewModel::iniciarEdicao,
            onCancelarEdicao = viewModel::cancelarEdicao,
            onAbrirSobre = onAbrirSobre,
            onVerIntroducao = viewModel::verIntroducao,
        ),
    )
}

/** Callbacks da tela agrupados para manter a assinatura curta. */
data class PerfilAcoes(
    val onTipoChange: (TipoPerfil) -> Unit = {},
    val onNomeChange: (String) -> Unit = {},
    val onCnpjChange: (String) -> Unit = {},
    val onEnderecoChange: (String) -> Unit = {},
    val onPreencherDemonstracao: (Perfil) -> Unit = {},
    val onSalvar: () -> Unit = {},
    val onEditar: () -> Unit = {},
    val onCancelarEdicao: () -> Unit = {},
    val onAbrirSobre: () -> Unit = {},
    val onVerIntroducao: () -> Unit = {},
)

@Composable
fun PerfilScreen(
    state: PerfilUiState,
    acoes: PerfilAcoes,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    onVoltar: (() -> Unit)? = null,
) {
    val titulo = stringResource(if (state.mostrarFormulario) R.string.perfil_configurar_titulo else R.string.perfil_titulo)
    Scaffold(
        modifier = modifier,
        topBar = { PratinTopAppBar(titulo = titulo, onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        when {
            state.carregando -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.mostrarFormulario -> FormularioPerfil(state, acoes, Modifier.padding(padding))
            else -> ResumoPerfil(requireNotNull(state.perfil), acoes, Modifier.padding(padding))
        }
    }
}

@Composable
private fun FormularioPerfil(state: PerfilUiState, acoes: PerfilAcoes, modifier: Modifier = Modifier) {
    val form = state.form
    fun erro(campo: CampoPerfil) = state.erros[campo]?.mensagemRes()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(Dimens.margemTela),
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        Text(stringResource(R.string.perfil_tipo), style = MaterialTheme.typography.titleSmall)
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
            TipoPerfil.entries.forEachIndexed { indice, tipo ->
                SegmentedButton(
                    selected = form.tipo == tipo,
                    onClick = { acoes.onTipoChange(tipo) },
                    shape = SegmentedButtonDefaults.itemShape(indice, TipoPerfil.entries.size),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    icon = { Icon(tipo.icone(), contentDescription = null, modifier = Modifier.size(18.dp)) },
                ) { Text(stringResource(tipo.rotuloRes())) }
            }
        }
        erro(CampoPerfil.TIPO)?.let {
            Text(stringResource(it), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        PratinTextField(
            valor = form.nome,
            onValorChange = acoes.onNomeChange,
            rotulo = stringResource(R.string.campo_nome_perfil),
            erro = erro(CampoPerfil.NOME)?.let { stringResource(it) },
            capitalizacao = KeyboardCapitalization.Words,
        )
        PratinTextField(
            valor = form.cnpj,
            onValorChange = acoes.onCnpjChange,
            rotulo = stringResource(R.string.campo_cnpj),
            erro = erro(CampoPerfil.CNPJ)?.let { stringResource(it) },
            tipoTeclado = KeyboardType.Number,
            transformacao = CnpjVisualTransformation,
        )
        PratinTextField(
            valor = form.endereco,
            onValorChange = acoes.onEnderecoChange,
            rotulo = stringResource(R.string.campo_endereco),
            erro = erro(CampoPerfil.ENDERECO)?.let { stringResource(it) },
            acaoIme = ImeAction.Done,
        )

        MenuPerfisDemonstracao(onSelecionar = acoes.onPreencherDemonstracao)

        PrimaryButton(
            texto = stringResource(R.string.acao_salvar_perfil),
            onClick = acoes.onSalvar,
            carregando = state.salvando,
        )
        if (state.editando && state.perfil != null) {
            TextButton(onClick = acoes.onCancelarEdicao, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.acao_cancelar))
            }
        }
    }
}

/** Atalho para a demonstração: preenche o formulário com um dos perfis do seed (RF04-CA8). */
@Composable
private fun MenuPerfisDemonstracao(onSelecionar: (Perfil) -> Unit) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    Box {
        TextButton(onClick = { aberto = true }) {
            Icon(Icons.Filled.PersonSearch, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(Dimens.sm))
            Text(stringResource(R.string.acao_perfil_demonstracao))
        }
        DropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            SeedData.perfisDemonstracao.forEach { perfil ->
                DropdownMenuItem(
                    text = { Text("${perfil.nome} (${stringResource(perfil.tipo.rotuloRes())})") },
                    leadingIcon = { Icon(perfil.tipo.icone(), contentDescription = null) },
                    onClick = {
                        aberto = false
                        onSelecionar(perfil)
                    },
                )
            }
        }
    }
}

@Composable
private fun ResumoPerfil(perfil: Perfil, acoes: PerfilAcoes, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.margemTela),
        verticalArrangement = Arrangement.spacedBy(Dimens.md),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        ) {
            Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(perfil.tipo.icone(), contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.size(Dimens.md))
                    Column {
                        Text(perfil.nome, style = MaterialTheme.typography.titleLarge)
                        Text(
                            stringResource(perfil.tipo.rotuloRes()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                InfoRow(Icons.Filled.Badge, stringResource(R.string.campo_cnpj), Formatadores.cnpj(perfil.cnpj))
                InfoRow(Icons.Filled.LocationOn, stringResource(R.string.campo_endereco), perfil.endereco)
            }
        }
        OutlinedButton(onClick = acoes.onEditar, shape = BotaoShape, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(Dimens.sm))
            Text(stringResource(R.string.acao_editar_perfil))
        }
        TextButton(onClick = acoes.onAbrirSobre, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Info, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(Dimens.sm))
            Text(stringResource(R.string.sobre_titulo))
        }
        TextButton(onClick = acoes.onVerIntroducao, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.size(Dimens.sm))
            Text(stringResource(R.string.acao_ver_introducao))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PerfilFormularioPreview() {
    PratinTheme { PerfilScreen(state = PerfilUiState(carregando = false), acoes = PerfilAcoes()) }
}

@Preview(showBackground = true)
@Composable
private fun PerfilResumoPreview() {
    PratinTheme {
        PerfilScreen(state = PerfilUiState(carregando = false, perfil = SeedData.padaria), acoes = PerfilAcoes())
    }
}
