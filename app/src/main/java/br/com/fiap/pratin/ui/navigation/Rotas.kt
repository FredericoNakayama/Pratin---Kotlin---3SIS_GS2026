package br.com.fiap.pratin.ui.navigation

import kotlinx.serialization.Serializable

// Rotas type-safe do Navigation Compose (ADR-05). Destinos recebem apenas IDs.

@Serializable
data object SplashRoute

@Serializable
data object OnboardingRoute

/** Grafo aninhado das três abas da barra inferior. */
@Serializable
data object MainGraph

@Serializable
data object InicioRoute

@Serializable
data object AtividadesRoute

@Serializable
data object PerfilRoute

@Serializable
data class DetalhesRoute(val alimentoId: Long)

@Serializable
data object NovoAnuncioRoute

@Serializable
data class ConfirmacaoReservaRoute(val reservaId: Long)

/** Formulário de perfil em tela cheia, aberto pelo diálogo "Perfil necessário". */
@Serializable
data object EditarPerfilRoute

@Serializable
data object SobreRoute
