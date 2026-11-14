package br.com.fiap.pratin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.fiap.pratin.ui.PratinApp
import br.com.fiap.pratin.ui.theme.PratinTheme

/** Única Activity do app: splash do sistema, edge-to-edge e a interface em Compose. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PratinTheme {
                PratinApp()
            }
        }
    }
}
