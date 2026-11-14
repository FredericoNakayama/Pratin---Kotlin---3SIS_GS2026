package br.com.fiap.pratin.ui

import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.AnuncioDetalhado
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.Perfil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/** Troca o Dispatchers.Main (usado pelo viewModelScope) por um dispatcher de teste. */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
    override fun starting(description: Description) = Dispatchers.setMain(dispatcher)
    override fun finished(description: Description) = Dispatchers.resetMain()
}

class FakePerfilRepository(inicial: Perfil? = null) : PerfilRepository {
    private val estado = MutableStateFlow(inicial)
    override val perfil: Flow<Perfil?> = estado
    override suspend fun salvar(perfil: Perfil) {
        estado.value = perfil
    }
}

/** Repositório em memória: guarda os anúncios publicados e alterna favoritos. */
class FakeAlimentoRepository(iniciais: List<Alimento> = emptyList()) : AlimentoRepository {
    private val alimentos = MutableStateFlow(iniciais)
    val publicados = mutableListOf<NovoAnuncio>()

    override fun observarDisponiveis(): Flow<List<Alimento>> = alimentos
    override fun observarPorId(id: Long): Flow<Alimento?> = alimentos.map { lista -> lista.firstOrNull { it.id == id } }
    override fun observarAnuncios(cnpj: String): Flow<List<AnuncioDetalhado>> =
        alimentos.map { lista -> lista.filter { it.estabelecimentoCnpj == cnpj }.map { AnuncioDetalhado(it, null) } }

    override suspend fun publicar(form: NovoAnuncio, perfil: Perfil): Long {
        publicados += form
        return 100L + publicados.size
    }

    override suspend fun alternarFavorito(id: Long) = alimentos.update { lista ->
        lista.map { if (it.id == id) it.copy(favorito = !it.favorito) else it }
    }

    override suspend fun cancelarAnuncio(id: Long, perfil: Perfil) = Unit
    override suspend fun expirarVencidos() = Unit
}
