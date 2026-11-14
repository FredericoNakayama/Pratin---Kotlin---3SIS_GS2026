package br.com.fiap.pratin.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** Tipografia padrão do Material 3 (Roboto). */
val Typography = Typography()

/** Estilo do código de retirada: monoespaçado e espaçado para leitura em voz alta. */
val CodigoRetiradaStyle = TextStyle(
    fontFamily = FontFamily.Monospace,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    letterSpacing = 8.sp,
)
