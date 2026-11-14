package br.com.fiap.pratin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.components.TelaProvisoria
import br.com.fiap.pratin.ui.screens.onboarding.OnboardingRoute
import br.com.fiap.pratin.ui.screens.splash.SplashRoute

/** Grafo de navegação do app (spec/03-arquitetura.md §6.2). Telas recebem só IDs e callbacks. */
@Composable
fun PratinNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = SplashRoute, modifier = modifier) {

        composable<SplashRoute> {
            SplashRoute(
                onIrParaOnboarding = {
                    navController.navigate(OnboardingRoute) { popUpTo(SplashRoute) { inclusive = true } }
                },
                onIrParaInicio = {
                    navController.navigate(MainGraph) { popUpTo(SplashRoute) { inclusive = true } }
                },
            )
        }

        composable<OnboardingRoute> {
            OnboardingRoute(
                onConcluir = {
                    navController.navigate(MainGraph) { popUpTo(OnboardingRoute) { inclusive = true } }
                },
            )
        }

        navigation<MainGraph>(startDestination = InicioRoute) {
            composable<InicioRoute> { TelaProvisoria(stringResource(R.string.aba_inicio)) }
            composable<AtividadesRoute> { TelaProvisoria(stringResource(R.string.atividades_titulo)) }
            composable<PerfilRoute> { TelaProvisoria(stringResource(R.string.perfil_titulo)) }
        }

        composable<DetalhesRoute> {
            TelaProvisoria(stringResource(R.string.detalhes_quantidade), onVoltar = navController::navigateUp)
        }
        composable<NovoAnuncioRoute> {
            TelaProvisoria(stringResource(R.string.novo_anuncio_titulo), onVoltar = navController::navigateUp)
        }
        composable<ConfirmacaoReservaRoute> {
            TelaProvisoria(stringResource(R.string.confirmacao_titulo), onVoltar = navController::navigateUp)
        }
        composable<EditarPerfilRoute> {
            TelaProvisoria(stringResource(R.string.perfil_configurar_titulo), onVoltar = navController::navigateUp)
        }
        composable<SobreRoute> {
            TelaProvisoria(stringResource(R.string.sobre_titulo), onVoltar = navController::navigateUp)
        }
    }
}
