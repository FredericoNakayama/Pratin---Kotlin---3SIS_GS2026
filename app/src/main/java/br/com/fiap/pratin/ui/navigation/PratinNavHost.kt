package br.com.fiap.pratin.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import br.com.fiap.pratin.R
import br.com.fiap.pratin.ui.screens.atividades.AtividadesRoute
import br.com.fiap.pratin.ui.screens.confirmacao.ConfirmacaoRoute
import br.com.fiap.pratin.ui.screens.detalhes.DetalhesRoute
import br.com.fiap.pratin.ui.screens.inicio.InicioRoute
import br.com.fiap.pratin.ui.screens.novoanuncio.NovoAnuncioRoute
import br.com.fiap.pratin.ui.screens.onboarding.OnboardingRoute
import br.com.fiap.pratin.ui.screens.perfil.PerfilRoute
import br.com.fiap.pratin.ui.screens.sobre.SobreScreen
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
            composable<InicioRoute> { entrada ->
                val mensagemResultado by entrada.mensagemResultado().collectAsStateWithLifecycle()
                InicioRoute(
                    onAbrirDetalhes = { id -> navController.navigate(DetalhesRoute(id)) },
                    onAnunciar = { navController.navigate(NovoAnuncioRoute) },
                    onConfigurarPerfil = { navController.navigate(EditarPerfilRoute) },
                    onAbrirSobre = { navController.navigate(SobreRoute) },
                    mensagemResultado = mensagemResultado,
                    onMensagemResultadoExibida = entrada::limparMensagemResultado,
                )
            }
            composable<AtividadesRoute> { entrada ->
                val mensagemResultado by entrada.mensagemResultado().collectAsStateWithLifecycle()
                AtividadesRoute(
                    onConfigurarPerfil = { navController.navegarParaAba(AbaPrincipal.PERFIL) },
                    onAnunciar = { navController.navigate(NovoAnuncioRoute) },
                    mensagemResultado = mensagemResultado,
                    onMensagemResultadoExibida = entrada::limparMensagemResultado,
                )
            }
            composable<PerfilRoute> {
                PerfilRoute(
                    onAbrirSobre = { navController.navigate(SobreRoute) },
                    onAbrirIntroducao = { navController.abrirIntroducao() },
                )
            }
        }

        composable<DetalhesRoute> {
            DetalhesRoute(
                onVoltar = navController::navigateUp,
                // Detalhes sai da pilha: "voltar" na Confirmação leva ao Início (RF13-CA2).
                onReservado = { reservaId ->
                    navController.navigate(ConfirmacaoReservaRoute(reservaId)) {
                        popUpTo<DetalhesRoute> { inclusive = true }
                    }
                },
                onConfigurarPerfil = { navController.navigate(EditarPerfilRoute) },
            )
        }
        composable<NovoAnuncioRoute> {
            NovoAnuncioRoute(
                onVoltar = navController::navigateUp,
                onPublicado = { navController.voltarComMensagem(R.string.msg_anuncio_publicado) },
            )
        }
        composable<ConfirmacaoReservaRoute> {
            // Sai da Confirmação sem guardar estado: a tela não volta mais (RF14-CA3).
            ConfirmacaoRoute(
                onVerAtividades = {
                    navController.navigate(AtividadesRoute) {
                        popUpTo(InicioRoute)
                        launchSingleTop = true
                    }
                },
                onVoltarInicio = { navController.popBackStack(InicioRoute, inclusive = false) },
            )
        }
        composable<EditarPerfilRoute> {
            PerfilRoute(
                telaCheia = true,
                onVoltar = navController::navigateUp,
                onAbrirSobre = { navController.navigate(SobreRoute) },
                onAbrirIntroducao = { navController.abrirIntroducao() },
            )
        }
        composable<SobreRoute> {
            SobreScreen(onVoltar = navController::navigateUp)
        }
    }
}

/** "Ver introdução novamente": reabre o Onboarding no lugar das abas. */
private fun NavHostController.abrirIntroducao() {
    navigate(OnboardingRoute) { popUpTo(MainGraph) { inclusive = true } }
}
