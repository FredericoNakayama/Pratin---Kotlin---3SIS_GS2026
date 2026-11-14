package br.com.fiap.pratin.ui.screens.novoanuncio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.error.CampoAnuncio
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.Unidade
import br.com.fiap.pratin.domain.rules.AnuncioValidator
import br.com.fiap.pratin.ui.components.AlimentoImagem
import br.com.fiap.pratin.ui.components.DateField
import br.com.fiap.pratin.ui.components.DateTimeField
import br.com.fiap.pratin.ui.components.PratinTextField
import br.com.fiap.pratin.ui.components.PratinTopAppBar
import br.com.fiap.pratin.ui.components.PrimaryButton
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme
import br.com.fiap.pratin.ui.util.asString
import br.com.fiap.pratin.ui.util.icone
import br.com.fiap.pratin.ui.util.mensagemRes
import br.com.fiap.pratin.ui.util.rotuloRes
import java.time.LocalDateTime
import java.time.LocalTime

@Composable
fun NovoAnuncioRoute(
    onVoltar: () -> Unit,
    onPublicado: () -> Unit,
    viewModel: NovoAnuncioViewModel = viewModel(factory = NovoAnuncioViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val mensagem = state.mensagem?.asString()

    LaunchedEffect(state.publicadoId) { if (state.publicadoId != null) onPublicado() }
    LaunchedEffect(mensagem) {
        if (mensagem != null) {
            snackbar.showSnackbar(mensagem)
            viewModel.mensagemExibida()
        }
    }

    NovoAnuncioScreen(
        state = state,
        snackbar = snackbar,
        onVoltar = onVoltar,
        acoes = NovoAnuncioAcoes(
            onNomeChange = viewModel::onNomeChange,
            onCategoriaChange = viewModel::onCategoriaChange,
            onQuantidadeChange = viewModel::onQuantidadeChange,
            onUnidadeChange = viewModel::onUnidadeChange,
            onValidadeChange = viewModel::onValidadeChange,
            onRetiradaInicioChange = viewModel::onRetiradaInicioChange,
            onRetiradaFimChange = viewModel::onRetiradaFimChange,
            onEnderecoChange = viewModel::onEnderecoChange,
            onFotoUrlChange = viewModel::onFotoUrlChange,
            onDeclaracaoChange = viewModel::onDeclaracaoChange,
            onPublicar = viewModel::publicar,
        ),
    )
}

data class NovoAnuncioAcoes(
    val onNomeChange: (String) -> Unit = {},
    val onCategoriaChange: (Categoria) -> Unit = {},
    val onQuantidadeChange: (String) -> Unit = {},
    val onUnidadeChange: (Unidade) -> Unit = {},
    val onValidadeChange: (java.time.LocalDate) -> Unit = {},
    val onRetiradaInicioChange: (LocalDateTime) -> Unit = {},
    val onRetiradaFimChange: (LocalDateTime) -> Unit = {},
    val onEnderecoChange: (String) -> Unit = {},
    val onFotoUrlChange: (String) -> Unit = {},
    val onDeclaracaoChange: (Boolean) -> Unit = {},
    val onPublicar: () -> Unit = {},
)

/** Novo anúncio (T07): o estabelecimento cadastra um lote (RF12). */
@Composable
fun NovoAnuncioScreen(
    state: NovoAnuncioUiState,
    acoes: NovoAnuncioAcoes,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
) {
    val form = state.form
    @Composable
    fun erro(campo: CampoAnuncio): String? = state.erros[campo]?.let { stringResource(it.mensagemRes()) }

    Scaffold(
        modifier = modifier,
        topBar = { PratinTopAppBar(titulo = stringResource(R.string.novo_anuncio_titulo), onVoltar = onVoltar) },
        snackbarHost = { SnackbarHost(snackbar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.margemTela),
            verticalArrangement = Arrangement.spacedBy(Dimens.sm),
        ) {
            PratinTextField(
                valor = form.nome,
                onValorChange = acoes.onNomeChange,
                rotulo = stringResource(R.string.campo_nome_alimento),
                erro = erro(CampoAnuncio.NOME),
                apoio = "${form.nome.length}/${AnuncioValidator.NOME_MAXIMO}",
            )
            SeletorEnum(
                rotulo = stringResource(R.string.campo_categoria),
                opcoes = Categoria.entries,
                selecionada = form.categoria,
                texto = { stringResource(it.rotuloRes()) },
                icone = { it.icone() },
                onSelecionar = acoes.onCategoriaChange,
                erro = erro(CampoAnuncio.CATEGORIA),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                PratinTextField(
                    valor = form.quantidadeTexto,
                    onValorChange = acoes.onQuantidadeChange,
                    rotulo = stringResource(R.string.campo_quantidade),
                    erro = erro(CampoAnuncio.QUANTIDADE),
                    tipoTeclado = KeyboardType.Decimal,
                    modifier = Modifier.weight(1f),
                )
                SeletorEnum(
                    rotulo = stringResource(R.string.campo_unidade),
                    opcoes = Unidade.entries,
                    selecionada = form.unidade,
                    texto = { stringResource(it.rotuloRes()) },
                    onSelecionar = acoes.onUnidadeChange,
                    erro = erro(CampoAnuncio.UNIDADE),
                    modifier = Modifier.weight(1f),
                )
            }
            DateField(
                valor = form.dataValidade,
                onValorChange = acoes.onValidadeChange,
                rotulo = stringResource(R.string.campo_validade),
                primeiroDia = state.hoje,
                ultimoDia = state.ultimoDiaValidade,
                erro = erro(CampoAnuncio.VALIDADE),
            )
            val erroJanela = erro(CampoAnuncio.JANELA)
            DateTimeField(
                valor = form.retiradaInicio,
                onValorChange = acoes.onRetiradaInicioChange,
                rotulo = stringResource(R.string.campo_retirada_inicio),
                primeiroDia = state.hoje,
                ultimoDia = state.ultimoDiaRetirada,
                horaSugerida = state.agora.toLocalTime().plusHours(1).withMinute(0),
                erro = erroJanela?.let { "" },
            )
            DateTimeField(
                valor = form.retiradaFim,
                onValorChange = acoes.onRetiradaFimChange,
                rotulo = stringResource(R.string.campo_retirada_fim),
                primeiroDia = form.retiradaInicio?.toLocalDate() ?: state.hoje,
                ultimoDia = state.ultimoDiaRetirada,
                horaSugerida = (form.retiradaInicio?.toLocalTime() ?: state.agora.toLocalTime()).plusHours(2).withMinute(0),
                erro = erroJanela,
            )
            PratinTextField(
                valor = form.endereco,
                onValorChange = acoes.onEnderecoChange,
                rotulo = stringResource(R.string.campo_endereco),
                erro = erro(CampoAnuncio.ENDERECO),
            )
            PratinTextField(
                valor = form.fotoUrl,
                onValorChange = acoes.onFotoUrlChange,
                rotulo = stringResource(R.string.campo_foto_url),
                erro = erro(CampoAnuncio.FOTO_URL),
                tipoTeclado = KeyboardType.Uri,
                capitalizacao = KeyboardCapitalization.None,
                acaoIme = ImeAction.Done,
            )
            if (form.fotoUrl.startsWith("http")) {
                AlimentoImagem(
                    fotoUrl = form.fotoUrl,
                    categoria = form.categoria ?: Categoria.OUTROS,
                    descricao = form.nome,
                    forma = MaterialTheme.shapes.medium,
                    modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                )
            }
            Declaracao(form.declaracaoProprio, acoes.onDeclaracaoChange, erro(CampoAnuncio.DECLARACAO))
            Spacer(Modifier.size(Dimens.sm))
            PrimaryButton(
                texto = stringResource(R.string.acao_publicar),
                onClick = acoes.onPublicar,
                carregando = state.publicando,
                icone = Icons.Filled.Campaign,
            )
        }
    }
}

@Composable
private fun Declaracao(marcada: Boolean, onChange: (Boolean) -> Unit, erro: String?) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = Dimens.toqueMinimo)
                .toggleable(value = marcada, role = Role.Checkbox, onValueChange = onChange),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = marcada, onCheckedChange = null)
            Spacer(Modifier.size(Dimens.sm))
            Text(stringResource(R.string.campo_declaracao), style = MaterialTheme.typography.bodyMedium)
        }
        if (erro != null) {
            Text(
                erro,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = Dimens.lg),
            )
        }
    }
}

/** Lista suspensa de um enum (categoria, unidade). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SeletorEnum(
    rotulo: String,
    opcoes: List<T>,
    selecionada: T?,
    texto: @Composable (T) -> String,
    onSelecionar: (T) -> Unit,
    modifier: Modifier = Modifier,
    icone: ((T) -> ImageVector)? = null,
    erro: String? = null,
) {
    var aberto by rememberSaveable { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = aberto, onExpandedChange = { aberto = it }, modifier = modifier) {
        OutlinedTextField(
            value = selecionada?.let { texto(it) }.orEmpty(),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(rotulo) },
            isError = erro != null,
            supportingText = erro?.let { { Text(it) } },
            leadingIcon = if (icone != null && selecionada != null) {
                { Icon(icone(selecionada), contentDescription = null, modifier = Modifier.size(20.dp)) }
            } else {
                null
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = aberto) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = aberto, onDismissRequest = { aberto = false }) {
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(texto(opcao)) },
                    leadingIcon = icone?.let { { Icon(it(opcao), contentDescription = null) } },
                    onClick = {
                        aberto = false
                        onSelecionar(opcao)
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NovoAnuncioPreview() {
    PratinTheme { NovoAnuncioScreen(state = NovoAnuncioUiState(), acoes = NovoAnuncioAcoes(), onVoltar = {}) }
}
