package br.com.fiap.pratin.domain.rules

import java.security.SecureRandom
import java.util.Random

/** Gera e confere o código de retirada de uma reserva (RN07). */
object CodigoGenerator {
    /** Sem O, 0, I e 1, que se confundem na leitura. */
    const val ALFABETO = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    const val TAMANHO = 6

    fun gerar(random: Random = SecureRandom()): String =
        buildString(TAMANHO) {
            repeat(TAMANHO) { append(ALFABETO[random.nextInt(ALFABETO.length)]) }
        }

    /** Compara o código digitado com o esperado, sem diferenciar maiúsculas. */
    fun confere(digitado: String, esperado: String): Boolean =
        digitado.trim().uppercase() == esperado.trim().uppercase()
}
