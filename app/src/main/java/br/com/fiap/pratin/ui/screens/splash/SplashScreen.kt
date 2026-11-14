package br.com.fiap.pratin.ui.screens.splash

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.theme.Dimens
import br.com.fiap.pratin.ui.theme.PratinTheme

@Composable
fun SplashRoute(
    onIrParaOnboarding: () -> Unit,
    onIrParaInicio: () -> Unit,
    viewModel: SplashViewModel = viewModel(factory = SplashViewModel.Factory),
) {
    val destino by viewModel.destino.collectAsStateWithLifecycle()
    LaunchedEffect(destino) {
        when (destino) {
            DestinoSplash.ONBOARDING -> onIrParaOnboarding()
            DestinoSplash.INICIO -> onIrParaInicio()
            null -> Unit
        }
    }
    IconesClarosNaBarraDeStatus()
    SplashScreen()
}

/** Fundo verde com logo, nome e slogan (T01), igual ao splash do sistema (ADR-09). */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .padding(Dimens.xl),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.logo_pratin),
                contentDescription = stringResource(R.string.cd_logo),
                modifier = Modifier.size(140.dp),
            )
        }
        Spacer(Modifier.height(Dimens.xl))
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onPrimary,
        )
        Spacer(Modifier.height(Dimens.sm))
        Text(
            text = stringResource(R.string.slogan),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(Dimens.xxl))
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 3.dp,
        )
    }
}

/** Ícones brancos na barra de status enquanto o fundo é verde; restaura ao sair da Splash. */
@Composable
private fun IconesClarosNaBarraDeStatus() {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(view) {
        val window = (view.context as? Activity)?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, view) }
        val anterior = controller?.isAppearanceLightStatusBars
        controller?.isAppearanceLightStatusBars = false
        onDispose { if (anterior != null) controller.isAppearanceLightStatusBars = anterior }
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    PratinTheme { SplashScreen() }
}
