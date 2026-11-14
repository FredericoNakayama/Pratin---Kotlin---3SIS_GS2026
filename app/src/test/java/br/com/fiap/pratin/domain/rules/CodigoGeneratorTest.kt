package br.com.fiap.pratin.domain.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Random

/** RN07 — código de retirada. */
class CodigoGeneratorTest {

    @Test
    fun `gera código com 6 caracteres do alfabeto`() {
        repeat(200) {
            val codigo = CodigoGenerator.gerar()
            assertEquals(6, codigo.length)
            assertTrue(codigo, codigo.all { it in CodigoGenerator.ALFABETO })
        }
    }

    @Test
    fun `alfabeto não tem caracteres ambíguos`() {
        listOf('O', '0', 'I', '1').forEach { assertFalse(CodigoGenerator.ALFABETO.contains(it)) }
    }

    @Test
    fun `mesma semente gera o mesmo código`() {
        assertEquals(CodigoGenerator.gerar(Random(42)), CodigoGenerator.gerar(Random(42)))
    }

    @Test
    fun `confere ignorando espaços e maiúsculas`() {
        assertTrue(CodigoGenerator.confere(" px7k2q ", "PX7K2Q"))
    }

    @Test
    fun `não confere código diferente`() {
        assertFalse(CodigoGenerator.confere("PX7K2R", "PX7K2Q"))
    }
}
