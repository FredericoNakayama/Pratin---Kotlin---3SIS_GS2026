package br.com.fiap.pratin.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta do Pratin (spec/04-ui.md §1). Telas usam MaterialTheme ou PratinColors, nunca cores soltas.
val VerdePrimario = Color(0xFF2E7D32)
val VerdeClaro = Color(0xFFC8E6C9)
val VerdeEscuro = Color(0xFF0A3D0F)
val LaranjaSecundario = Color(0xFFF57C00)
val LaranjaClaro = Color(0xFFFFE0B2)
val LaranjaEscuro = Color(0xFF4A2500)
val Fundo = Color(0xFFFAFAF7)
val Superficie = Color(0xFFFFFFFF)
val SuperficieVariante = Color(0xFFF1F3EE)
val Erro = Color(0xFFC62828)
val TextoPrimario = Color(0xFF1B1B1B)
val TextoSecundario = Color(0xFF5F6368)
val Divisor = Color(0xFFE0E0E0)

/** Cores semânticas que não fazem parte do ColorScheme do Material 3. */
object PratinColors {
    val favorito = Color(0xFFE53935)
    val statusDisponivel = Color(0xFF2E7D32)
    val statusReservado = Color(0xFFFFA000)
    val statusRetirado = Color(0xFF1565C0)
    val statusCancelado = Color(0xFF757575)
    val statusExpirado = Color(0xFFB71C1C)
}
