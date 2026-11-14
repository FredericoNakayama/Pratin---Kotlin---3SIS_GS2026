package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.AGORA
import br.com.fiap.pratin.domain.HOJE
import br.com.fiap.pratin.domain.anuncioValido
import br.com.fiap.pratin.domain.error.CampoAnuncio
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.NovoAnuncio
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** RN03 — anúncio válido. Cada teste quebra uma regra isolada. */
class AnuncioValidatorTest {

    private fun erros(form: NovoAnuncio) = AnuncioValidator.validar(form, AGORA)
    private fun erroDe(form: NovoAnuncio, campo: CampoAnuncio) = erros(form)[campo]

    @Test
    fun `anúncio completo é válido`() {
        assertTrue(erros(anuncioValido()).isEmpty())
    }

    @Test
    fun `formulário vazio mostra erro em todos os campos obrigatórios`() {
        val erros = erros(NovoAnuncio())
        val esperados = CampoAnuncio.entries.toSet() - CampoAnuncio.FOTO_URL
        assertEquals(esperados, erros.keys)
    }

    // Nome
    @Test
    fun `nome precisa de 2 a 60 caracteres sem contar espaços nas pontas`() {
        assertEquals(ErroCampo.NOME_ALIMENTO, erroDe(anuncioValido().copy(nome = " P "), CampoAnuncio.NOME))
        assertNull(erroDe(anuncioValido().copy(nome = "Pã"), CampoAnuncio.NOME))
        assertNull(erroDe(anuncioValido().copy(nome = "a".repeat(60)), CampoAnuncio.NOME))
        assertEquals(ErroCampo.NOME_ALIMENTO, erroDe(anuncioValido().copy(nome = "a".repeat(61)), CampoAnuncio.NOME))
    }

    // Categoria e unidade
    @Test
    fun `categoria e unidade são obrigatórias`() {
        assertEquals(ErroCampo.CATEGORIA, erroDe(anuncioValido().copy(categoria = null), CampoAnuncio.CATEGORIA))
        assertEquals(ErroCampo.UNIDADE, erroDe(anuncioValido().copy(unidade = null), CampoAnuncio.UNIDADE))
    }

    // Quantidade
    @Test
    fun `quantidade aceita decimal com vírgula ou ponto`() {
        assertNull(erroDe(anuncioValido().copy(quantidadeTexto = "8,5"), CampoAnuncio.QUANTIDADE))
        assertNull(erroDe(anuncioValido().copy(quantidadeTexto = "8.5"), CampoAnuncio.QUANTIDADE))
        assertEquals(8.5, AnuncioValidator.lerQuantidade(" 8,5 ")!!, 0.0)
    }

    @Test
    fun `quantidade precisa ser número maior que zero`() {
        listOf("0", "-1", "abc", "").forEach {
            assertEquals(it, ErroCampo.QUANTIDADE, erroDe(anuncioValido().copy(quantidadeTexto = it), CampoAnuncio.QUANTIDADE))
        }
    }

    // Validade
    @Test
    fun `validade é obrigatória`() {
        assertEquals(
            ErroCampo.VALIDADE_OBRIGATORIA,
            erroDe(anuncioValido().copy(dataValidade = null), CampoAnuncio.VALIDADE),
        )
    }

    @Test
    fun `validade entre hoje e hoje mais 7 dias`() {
        assertNull(erroDe(anuncioValido().copy(dataValidade = HOJE), CampoAnuncio.VALIDADE))
        assertNull(erroDe(anuncioValido().copy(dataValidade = HOJE.plusDays(7)), CampoAnuncio.VALIDADE))
        assertEquals(
            ErroCampo.VALIDADE_FORA_DO_PRAZO,
            erroDe(anuncioValido().copy(dataValidade = HOJE.plusDays(8)), CampoAnuncio.VALIDADE),
        )
        assertEquals(
            ErroCampo.VALIDADE_FORA_DO_PRAZO,
            erroDe(anuncioValido().copy(dataValidade = HOJE.minusDays(1)), CampoAnuncio.VALIDADE),
        )
    }

    // Janela de retirada
    @Test
    fun `janela é obrigatória`() {
        assertEquals(
            ErroCampo.JANELA_OBRIGATORIA,
            erroDe(anuncioValido().copy(retiradaInicio = null), CampoAnuncio.JANELA),
        )
        assertEquals(
            ErroCampo.JANELA_OBRIGATORIA,
            erroDe(anuncioValido().copy(retiradaFim = null), CampoAnuncio.JANELA),
        )
    }

    @Test
    fun `janela precisa de pelo menos 1 hora`() {
        val inicio = AGORA.plusHours(1)
        assertEquals(
            ErroCampo.JANELA_CURTA,
            erroDe(anuncioValido().copy(retiradaInicio = inicio, retiradaFim = inicio.plusMinutes(59)), CampoAnuncio.JANELA),
        )
        assertNull(
            erroDe(anuncioValido().copy(retiradaInicio = inicio, retiradaFim = inicio.plusMinutes(60)), CampoAnuncio.JANELA),
        )
    }

    @Test
    fun `início tolera 5 minutos no passado`() {
        assertNull(
            erroDe(anuncioValido().copy(retiradaInicio = AGORA.minusMinutes(5)), CampoAnuncio.JANELA),
        )
        assertEquals(
            ErroCampo.JANELA_NO_PASSADO,
            erroDe(anuncioValido().copy(retiradaInicio = AGORA.minusMinutes(6)), CampoAnuncio.JANELA),
        )
    }

    @Test
    fun `fim precisa estar no futuro`() {
        assertEquals(
            ErroCampo.JANELA_NO_PASSADO,
            erroDe(
                anuncioValido().copy(retiradaInicio = AGORA.minusHours(2), retiradaFim = AGORA),
                CampoAnuncio.JANELA,
            ),
        )
    }

    @Test
    fun `retirada termina até 23h59min59s do dia da validade`() {
        val validade = HOJE.plusDays(1)
        val limite = validade.atTime(23, 59, 59)
        assertNull(
            erroDe(
                anuncioValido().copy(dataValidade = validade, retiradaInicio = limite.minusHours(2), retiradaFim = limite),
                CampoAnuncio.JANELA,
            ),
        )
        assertEquals(
            ErroCampo.JANELA_APOS_VALIDADE,
            erroDe(
                anuncioValido().copy(
                    dataValidade = validade,
                    retiradaInicio = limite.minusHours(2),
                    retiradaFim = limite.plusSeconds(1),
                ),
                CampoAnuncio.JANELA,
            ),
        )
    }

    // Endereço, foto e declaração
    @Test
    fun `endereço precisa de 5 caracteres`() {
        assertEquals(ErroCampo.ENDERECO, erroDe(anuncioValido().copy(endereco = " Rua "), CampoAnuncio.ENDERECO))
        assertNull(erroDe(anuncioValido().copy(endereco = "Rua A"), CampoAnuncio.ENDERECO))
    }

    @Test
    fun `foto é opcional, mas se informada começa com http`() {
        assertNull(erroDe(anuncioValido().copy(fotoUrl = "  "), CampoAnuncio.FOTO_URL))
        assertNull(erroDe(anuncioValido().copy(fotoUrl = "https://exemplo.com/f.jpg"), CampoAnuncio.FOTO_URL))
        assertNull(erroDe(anuncioValido().copy(fotoUrl = "http://exemplo.com/f.jpg"), CampoAnuncio.FOTO_URL))
        assertEquals(ErroCampo.FOTO_URL, erroDe(anuncioValido().copy(fotoUrl = "ftp://x.com/f.jpg"), CampoAnuncio.FOTO_URL))
        assertEquals(ErroCampo.FOTO_URL, erroDe(anuncioValido().copy(fotoUrl = "foto.jpg"), CampoAnuncio.FOTO_URL))
    }

    @Test
    fun `declaração de próprio para consumo é obrigatória`() {
        assertEquals(
            ErroCampo.DECLARACAO,
            erroDe(anuncioValido().copy(declaracaoProprio = false), CampoAnuncio.DECLARACAO),
        )
    }
}
