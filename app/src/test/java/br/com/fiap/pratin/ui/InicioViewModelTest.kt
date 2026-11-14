package br.com.fiap.pratin.ui

import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.seed.SeedData
import br.com.fiap.pratin.domain.AGORA
import br.com.fiap.pratin.domain.alimentoDeTeste
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.StatusAlimento
import br.com.fiap.pratin.ui.screens.inicio.InicioViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.ZoneId

/** RF06–RF09 e RF21 — lista, busca, filtros e bloqueio sem perfil no Início. */
@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModelTest {

    @get:Rule
    val main = MainDispatcherRule()

    private val zona = ZoneId.of("America/Sao_Paulo")
    private val clock = Clock.fixed(AGORA.atZone(zona).toInstant(), zona)

    private val alimentos = FakeAlimentoRepository(
        listOf(
            alimentoDeTeste().copy(id = 1, nome = "Feijão carioca", categoria = Categoria.MERCEARIA),
            alimentoDeTeste().copy(id = 2, nome = "Pão de queijo", categoria = Categoria.PAES),
            alimentoDeTeste(retiradaInicio = AGORA.minusHours(3), retiradaFim = AGORA).copy(id = 3, nome = "Vencido"),
            alimentoDeTeste(status = StatusAlimento.RESERVADO).copy(id = 4, nome = "Reservado"),
        ),
    )

    private fun criar(perfil: br.com.fiap.pratin.domain.model.Perfil? = SeedData.ongMesaCheia) =
        InicioViewModel(alimentos, FakePerfilRepository(perfil), clock)

    private fun nomes(vm: InicioViewModel) = vm.uiState.value.alimentos.map { it.nome }

    @Test
    fun `mostra só disponíveis com a janela aberta`() = runTest {
        val vm = criar()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        assertEquals(listOf("Feijão carioca", "Pão de queijo"), nomes(vm))
    }

    @Test
    fun `busca ignora acentos e maiúsculas`() = runTest {
        val vm = criar()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        vm.onBuscaChange("FEIJAO")
        assertEquals(listOf("Feijão carioca"), nomes(vm))
    }

    @Test
    fun `busca sem resultado usa a mensagem de busca vazia`() = runTest {
        val vm = criar()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        vm.onBuscaChange("chocolate")
        assertTrue(vm.uiState.value.alimentos.isEmpty())
        assertEquals(R.string.msg_busca_vazia, vm.uiState.value.mensagemVazio)
    }

    @Test
    fun `filtro de categoria e de favoritos combinam`() = runTest {
        val vm = criar()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        vm.onCategoriaChange(Categoria.PAES)
        assertEquals(listOf("Pão de queijo"), nomes(vm))

        vm.onFavoritosChange(true)
        assertTrue(vm.uiState.value.alimentos.isEmpty())
        assertEquals(R.string.msg_favoritos_vazio, vm.uiState.value.mensagemVazio)

        vm.alternarFavorito(2)
        assertEquals(listOf("Pão de queijo"), nomes(vm))
    }

    @Test
    fun `sem perfil o FAB abre o diálogo de perfil necessário`() = runTest {
        val vm = criar(perfil = null)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        assertTrue(vm.uiState.value.podeAnunciar)
        assertFalse(vm.onAnunciar())
        assertTrue(vm.uiState.value.mostrarPerfilNecessario)
    }

    @Test
    fun `ONG não vê o botão Anunciar`() = runTest {
        val vm = criar(perfil = SeedData.ongMesaCheia)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        assertFalse(vm.uiState.value.podeAnunciar)
    }
}
