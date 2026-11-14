package br.com.fiap.pratin.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Mostra só dígitos como `00.000.000/0000-00` enquanto o usuário digita.
 * O valor guardado continua sendo apenas os dígitos (RN02).
 */
object CnpjVisualTransformation : VisualTransformation {
    /** Separador inserido antes do dígito de cada posição. */
    private val separadores = mapOf(2 to '.', 5 to '.', 8 to '/', 12 to '-')

    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        val formatado = buildString {
            digitos.forEachIndexed { indice, digito ->
                separadores[indice]?.let(::append)
                append(digito)
            }
        }
        val mapeamento = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                offset + separadores.keys.count { it < offset && it < digitos.length }

            override fun transformedToOriginal(offset: Int): Int =
                (0..digitos.length).firstOrNull { originalToTransformed(it) >= offset } ?: digitos.length
        }
        return TransformedText(AnnotatedString(formatado), mapeamento)
    }
}
