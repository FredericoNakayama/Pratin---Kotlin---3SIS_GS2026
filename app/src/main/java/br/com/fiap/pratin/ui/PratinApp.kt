package br.com.fiap.pratin.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.fiap.pratin.ui.navigation.AbaPrincipal
import br.com.fiap.pratin.ui.navigation.PratinNavHost
import br.com.fiap.pratin.ui.navigation.navegarParaAba

/**
 * Raiz da interface: barra inferior visível só nas três abas (RF03) e o grafo de navegação.
 * Cada tela tem o próprio Scaffold com TopAppBar e Snackbar.
 */
@Composable
fun PratinApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destinoAtual = backStackEntry?.destination
    val mostrarBarra = AbaPrincipal.entries.any { it.selecionadaEm(destinoAtual) }

    Scaffold(
        // As telas internas tratam as barras do sistema; aqui só reservamos o espaço da barra inferior.
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (mostrarBarra) {
                BarraInferior(destinoAtual = destinoAtual, onSelecionar = navController::navegarParaAba)
            }
        },
    ) { padding ->
        PratinNavHost(
            navController = navController,
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
        )
    }
}

@Composable
private fun BarraInferior(destinoAtual: NavDestination?, onSelecionar: (AbaPrincipal) -> Unit) {
    NavigationBar {
        AbaPrincipal.entries.forEach { aba ->
            val selecionada = aba.selecionadaEm(destinoAtual)
            NavigationBarItem(
                selected = selecionada,
                onClick = { onSelecionar(aba) },
                icon = { Icon(if (selecionada) aba.iconeSelecionado else aba.icone, contentDescription = null) },
                label = { Text(stringResource(aba.rotulo)) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}
