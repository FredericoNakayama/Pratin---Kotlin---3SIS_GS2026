package br.com.fiap.pratin.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable

/** Tela temporária usada enquanto a fase correspondente não é implementada. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaProvisoria(titulo: String, onVoltar: (() -> Unit)? = null) {
    Scaffold(topBar = { PratinTopAppBar(titulo = titulo, onVoltar = onVoltar) }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
        }
    }
}
