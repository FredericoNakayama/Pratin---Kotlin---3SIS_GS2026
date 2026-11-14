package br.com.fiap.pratin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PratinLightColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeEscuro,
    secondary = LaranjaSecundario,
    onSecondary = Color.White,
    secondaryContainer = LaranjaClaro,
    onSecondaryContainer = LaranjaEscuro,
    background = Fundo,
    onBackground = TextoPrimario,
    surface = Superficie,
    onSurface = TextoPrimario,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = TextoSecundario,
    surfaceContainerLowest = Superficie,
    surfaceContainerLow = Superficie,
    surfaceContainer = SuperficieVariante,
    surfaceContainerHigh = SuperficieVariante,
    outlineVariant = Divisor,
    error = Erro,
    onError = Color.White,
)

/**
 * Tema do Pratin. A cor dinâmica fica desligada porque o app tem identidade própria
 * (spec/04-ui.md §1) e os prints do README usam sempre o tema claro.
 */
@Composable
fun PratinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PratinLightColorScheme,
        typography = Typography,
        shapes = PratinShapes,
        content = content,
    )
}
