package br.com.fiap.pratin.ui.screens.sobre

import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.components.PratinTopAppBar
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme

/** Sobre o Pratin (T10): contextualização exigida pelo critério 1 da rubrica (RF05). */
@Composable
fun SobreScreen(onVoltar: () -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { PratinTopAppBar(titulo = stringResource(R.string.sobre_titulo), onVoltar = onVoltar) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(Dimens.margemTela),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
        ) {
            Cabecalho()
            Secao(Icons.Filled.Warning, R.string.sobre_problema_titulo) { Paragrafo(R.string.sobre_problema) }
            Secao(Icons.Filled.Groups, R.string.sobre_publico_titulo) { Paragrafo(R.string.sobre_publico) }
            Secao(Icons.Filled.Route, R.string.sobre_como_titulo) {
                listOf(R.string.sobre_passo_1, R.string.sobre_passo_2, R.string.sobre_passo_3)
                    .forEachIndexed { indice, passo -> Passo(numero = indice + 1, texto = passo) }
            }
            Secao(Icons.Filled.Public, R.string.sobre_ods_titulo) {
                Ods(R.string.sobre_ods2_titulo, R.string.sobre_ods2, cor = Color(0xFFDDA63A))
                Ods(R.string.sobre_ods12_titulo, R.string.sobre_ods12, cor = Color(0xFFBF8B2E))
            }
            Secao(Icons.Filled.Info, R.string.sobre_prototipo_titulo) { Paragrafo(R.string.sobre_prototipo) }
            Secao(Icons.Filled.School, R.string.sobre_equipe_titulo) { Paragrafo(R.string.sobre_equipe) }
        }
    }
}

@Composable
private fun Cabecalho() {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(R.drawable.logo_pratin),
            contentDescription = stringResource(R.string.cd_logo),
            modifier = Modifier.size(96.dp),
        )
        Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(R.string.slogan),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Secao(icone: ImageVector, @StringRes titulo: Int, conteudo: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.padding(Dimens.lg), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icone, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.size(Dimens.sm))
                Text(stringResource(titulo), style = MaterialTheme.typography.titleMedium)
            }
            conteudo()
        }
    }
}

@Composable
private fun Paragrafo(@StringRes texto: Int) {
    Text(stringResource(texto), style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun Passo(numero: Int, @StringRes texto: Int) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(numero.toString(), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelMedium)
        }
        Spacer(Modifier.size(Dimens.sm))
        Text(stringResource(texto), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Ods(@StringRes titulo: Int, @StringRes descricao: Int, cor: Color) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            Modifier
                .padding(top = Dimens.xs)
                .size(12.dp)
                .clip(CircleShape)
                .background(cor),
        )
        Spacer(Modifier.size(Dimens.sm))
        Column {
            Text(stringResource(titulo), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(stringResource(descricao), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SobreScreenPreview() {
    PratinTheme { SobreScreen(onVoltar = {}) }
}
