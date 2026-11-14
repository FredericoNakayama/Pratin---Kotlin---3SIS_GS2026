package br.com.fiap.pratin.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import br.com.fiap.pratin.R

/** Destinos da barra inferior (RF03). */
enum class AbaPrincipal(
    val rota: Any,
    @StringRes val rotulo: Int,
    val iconeSelecionado: ImageVector,
    val icone: ImageVector,
) {
    INICIO(InicioRoute, R.string.aba_inicio, Icons.Filled.Home, Icons.Outlined.Home),
    ATIVIDADES(AtividadesRoute, R.string.aba_atividades, Icons.AutoMirrored.Filled.ReceiptLong, Icons.AutoMirrored.Outlined.ReceiptLong),
    PERFIL(PerfilRoute, R.string.aba_perfil, Icons.Filled.Person, Icons.Outlined.Person),
    ;

    fun selecionadaEm(destino: NavDestination?): Boolean =
        destino?.hierarchy?.any { it.hasRoute(rota::class) } == true
}

/**
 * Troca de aba sem empilhar telas: volta até o Início guardando o estado, e não duplica a aba
 * atual ao reselecioná-la (RF03-CA1 e CA2).
 */
fun NavController.navegarParaAba(aba: AbaPrincipal) {
    navigate(aba.rota) {
        popUpTo(InicioRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
