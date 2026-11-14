package br.com.fiap.pratin.ui.screens.onboarding

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NoFood
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.theme.BotaoShape
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme
import kotlinx.coroutines.launch

/** Conteúdo de uma página do onboarding (textos em spec/04-ui.md §6). */
private data class PaginaOnboarding(
    val icone: ImageVector,
    @StringRes val titulo: Int,
    @StringRes val descricao: Int,
)

private val paginas = listOf(
    PaginaOnboarding(Icons.Filled.NoFood, R.string.onboarding_titulo_1, R.string.onboarding_descricao_1),
    PaginaOnboarding(Icons.Filled.Storefront, R.string.onboarding_titulo_2, R.string.onboarding_descricao_2),
    PaginaOnboarding(Icons.Filled.VolunteerActivism, R.string.onboarding_titulo_3, R.string.onboarding_descricao_3),
)

@Composable
fun OnboardingRoute(
    onConcluir: () -> Unit,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.concluido) { if (state.concluido) onConcluir() }
    OnboardingScreen(
        naoMostrarNovamente = state.naoMostrarNovamente,
        onNaoMostrarChange = viewModel::onNaoMostrarChange,
        onComecar = viewModel::concluir,
    )
}

@Composable
fun OnboardingScreen(
    naoMostrarNovamente: Boolean,
    onNaoMostrarChange: (Boolean) -> Unit,
    onComecar: () -> Unit,
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState { paginas.size },
) {
    val scope = rememberCoroutineScope()
    val ultimaPagina = pagerState.currentPage == paginas.lastIndex

    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(Dimens.lg),
        ) {
            // "Pular" leva à última página (RF02-CA2).
            Box(Modifier.fillMaxWidth().heightIn(min = Dimens.toqueMinimo), contentAlignment = Alignment.CenterEnd) {
                if (!ultimaPagina) {
                    TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(paginas.lastIndex) } }) {
                        Text(stringResource(R.string.onboarding_pular))
                    }
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { indice ->
                PaginaConteudo(paginas[indice])
            }

            IndicadorPaginas(total = paginas.size, atual = pagerState.currentPage)
            Spacer(Modifier.height(Dimens.lg))

            if (ultimaPagina) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = Dimens.toqueMinimo)
                        .toggleable(
                            value = naoMostrarNovamente,
                            role = Role.Checkbox,
                            onValueChange = onNaoMostrarChange,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = naoMostrarNovamente, onCheckedChange = null)
                    Spacer(Modifier.size(Dimens.sm))
                    Text(stringResource(R.string.onboarding_nao_mostrar), style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(Dimens.sm))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // "Voltar" escondido na 1ª página (RF02-CA1).
                if (pagerState.currentPage > 0) {
                    TextButton(onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }) {
                        Text(stringResource(R.string.onboarding_voltar))
                    }
                } else {
                    Spacer(Modifier.size(Dimens.toqueMinimo))
                }
                Button(
                    shape = BotaoShape,
                    onClick = {
                        if (ultimaPagina) {
                            onComecar()
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                ) {
                    Text(stringResource(if (ultimaPagina) R.string.onboarding_comecar else R.string.onboarding_avancar))
                }
            }
        }
    }
}

@Composable
private fun PaginaConteudo(pagina: PaginaOnboarding) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = Dimens.lg),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = pagina.icone,
                contentDescription = null,
                modifier = Modifier.size(88.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.height(Dimens.xxl))
        Text(
            text = stringResource(pagina.titulo),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.md))
        Text(
            text = stringResource(pagina.descricao),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun IndicadorPaginas(total: Int, atual: Int) {
    val descricao = stringResource(R.string.cd_pagina_atual, atual + 1, total)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = descricao },
        horizontalArrangement = Arrangement.Center,
    ) {
        repeat(total) { indice ->
            val selecionada = indice == atual
            Box(
                modifier = Modifier
                    .padding(horizontal = Dimens.xs)
                    .size(if (selecionada) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (selecionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    PratinTheme { OnboardingScreen(naoMostrarNovamente = false, onNaoMostrarChange = {}, onComecar = {}) }
}
