package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.theme.BotaoShape
import br.com.fiap.pratin.ui.theme.Dimens

/** Indicador de carregamento centralizado. */
@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
}

/**
 * Estado sem dados (critério 3 da rubrica): ícone, mensagem e uma ação opcional.
 * É rolável para funcionar dentro do "puxar para atualizar".
 */
@Composable
fun EmptyState(
    icone: ImageVector,
    mensagem: String,
    modifier: Modifier = Modifier,
    textoAcao: String? = null,
    onAcao: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.xxl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.outlineVariant,
        )
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = Dimens.lg),
        )
        if (textoAcao != null && onAcao != null) {
            OutlinedButton(onClick = onAcao, shape = BotaoShape, modifier = Modifier.padding(top = Dimens.lg)) {
                Text(textoAcao)
            }
        }
    }
}

/** Erro inesperado com "Tentar novamente". */
@Composable
fun ErrorState(mensagem: String, onTentarNovamente: () -> Unit, modifier: Modifier = Modifier) {
    EmptyState(
        icone = Icons.Filled.ErrorOutline,
        mensagem = mensagem,
        modifier = modifier,
        textoAcao = stringResource(R.string.acao_tentar_novamente),
        onAcao = onTentarNovamente,
    )
}
