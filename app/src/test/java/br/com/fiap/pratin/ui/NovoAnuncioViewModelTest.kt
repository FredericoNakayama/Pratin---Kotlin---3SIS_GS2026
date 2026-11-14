package br.com.fiap.pratin.ui

import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.domain.AGORA
import br.com.fiap.pratin.domain.HOJE
import br.com.fiap.pratin.domain.error.CampoAnuncio
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.Unidade
import br.com.fiap.pratin.ui.screens.novoanuncio.NovoAnuncioViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.ZoneId

/** RF12 — o formulário só grava quando passa na RN03. */
class NovoAnuncioViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val zona = ZoneId.of("America/Sao_Paulo")
    private val clock = Clock.fixed(AGORA.atZone(zona).toInstant(), zona)
    private val alimentos = FakeAlimentoRepository()
    private fun criar() = NovoAnuncioViewModel(FakePerfilRepository(SeedData.padaria), alimentos, clock)

    @Test
    fun `endereço vem preenchido com o do perfil`() {
        assertEquals(SeedData.padaria.endereco, criar().uiState.value.form.endereco)
    }

    @Test
    fun `formulário inválido mostra os erros e não grava nada`() {
        val vm = criar()
        vm.publicar()

        val estado = vm.uiState.value
        assertTrue(CampoAnuncio.NOME in estado.erros)
        assertTrue(CampoAnuncio.JANELA in estado.erros)
        assertNull(estado.publicadoId)
        assertTrue(alimentos.publicados.isEmpty())
    }

    @Test
    fun `editar um campo apaga só o erro dele`() {
        val vm = criar()
        vm.publicar()
        vm.onNomeChange("Pão de forma")

        val erros = vm.uiState.value.erros
        assertTrue(CampoAnuncio.NOME !in erros)
        assertTrue(CampoAnuncio.CATEGORIA in erros)
    }

    @Test
    fun `formulário válido grava e informa o id criado`() {
        val vm = criar()
        vm.onNomeChange("Pão de forma")
        vm.onCategoriaChange(Categoria.PAES)
        vm.onQuantidadeChange("12")
        vm.onUnidadeChange(Unidade.PACOTE)
        vm.onValidadeChange(HOJE.plusDays(2))
        vm.onRetiradaInicioChange(AGORA.plusHours(1))
        vm.onRetiradaFimChange(AGORA.plusHours(3))
        vm.onDeclaracaoChange(true)
        vm.publicar()

        val estado = vm.uiState.value
        assertTrue(estado.erros.isEmpty())
        assertNotNull(estado.publicadoId)
        assertEquals(1, alimentos.publicados.size)
        assertEquals("Pão de forma", alimentos.publicados.single().nome)
    }

    @Test
    fun `quantidade aceita só dígitos e separador decimal`() {
        val vm = criar()
        vm.onQuantidadeChange("8,5kg")
        assertEquals("8,5", vm.uiState.value.form.quantidadeTexto)
    }
}
