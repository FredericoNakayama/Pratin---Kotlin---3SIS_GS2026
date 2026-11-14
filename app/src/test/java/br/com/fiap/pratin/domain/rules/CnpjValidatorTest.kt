package br.com.fiap.pratin.domain.rules

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** RN02 — validação de CNPJ pelos dígitos verificadores. */
class CnpjValidatorTest {

    @Test
    fun `aceita CNPJ válido com pontuação`() {
        assertTrue(CnpjValidator.isValido("11.222.333/0001-81"))
    }

    @Test
    fun `aceita CNPJ válido só com dígitos`() {
        assertTrue(CnpjValidator.isValido("11222333000181"))
    }

    @Test
    fun `aceita todos os CNPJs de demonstração`() {
        listOf(
            "11222333000181", "11444777000161", "98765432000198",
            "33444555000181", "55666777000181", "12345678000195",
        ).forEach { assertTrue(it, CnpjValidator.isValido(it)) }
    }

    @Test
    fun `rejeita todos os dígitos iguais`() {
        assertFalse(CnpjValidator.isValido("11111111111111"))
        assertFalse(CnpjValidator.isValido("00000000000000"))
    }

    @Test
    fun `rejeita tamanho diferente de 14 dígitos`() {
        assertFalse(CnpjValidator.isValido("1122233300018"))
        assertFalse(CnpjValidator.isValido("112223330001811"))
    }

    @Test
    fun `rejeita dígito verificador errado`() {
        assertFalse(CnpjValidator.isValido("11222333000182"))
        assertFalse(CnpjValidator.isValido("11222333000191"))
    }

    @Test
    fun `rejeita nulo e vazio`() {
        assertFalse(CnpjValidator.isValido(null))
        assertFalse(CnpjValidator.isValido(""))
    }

    @Test
    fun `remove tudo que não for dígito`() {
        assertEquals("11222333000181", CnpjValidator.apenasDigitos("11.222.333/0001-81"))
    }
}
