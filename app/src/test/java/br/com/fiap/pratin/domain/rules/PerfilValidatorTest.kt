package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.error.CampoPerfil
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.TipoPerfil
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** RN17 — perfil válido. */
class PerfilValidatorTest {

    @Test
    fun `perfil completo é válido`() {
        val erros = PerfilValidator.validar(TipoPerfil.ONG, "ONG Mesa Cheia", "12.345.678/0001-95", "Rua da Esperança, 45")
        assertTrue(erros.isEmpty())
    }

    @Test
    fun `formulário vazio mostra erro em todos os campos`() {
        val erros = PerfilValidator.validar(null, "", "", "")
        assertEquals(
            mapOf(
                CampoPerfil.TIPO to ErroCampo.TIPO_PERFIL,
                CampoPerfil.NOME to ErroCampo.NOME_PERFIL,
                CampoPerfil.CNPJ to ErroCampo.CNPJ,
                CampoPerfil.ENDERECO to ErroCampo.ENDERECO,
            ),
            erros,
        )
    }

    @Test
    fun `nome precisa de 3 caracteres e endereço de 5`() {
        val erros = PerfilValidator.validar(TipoPerfil.ESTABELECIMENTO, " AB ", "11222333000181", "Rua")
        assertEquals(setOf(CampoPerfil.NOME, CampoPerfil.ENDERECO), erros.keys)
    }

    @Test
    fun `CNPJ inválido é rejeitado`() {
        val erros = PerfilValidator.validar(TipoPerfil.ONG, "ONG Teste", "11222333000182", "Rua Teste, 1")
        assertEquals(mapOf(CampoPerfil.CNPJ to ErroCampo.CNPJ), erros)
    }
}
