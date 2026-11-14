package br.com.fiap.pratin.ui.screens.inicio

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.domain.format.Formatadores
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.rules.RegrasStatus
import br.com.fiap.pratin.domain.rules.agora
import br.com.fiap.pratin.ui.util.UiText
import br.com.fiap.pratin.ui.util.appContainer
import br.com.fiap.pratin.ui.util.paraUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

data class InicioUiState(
    val carregando: Boolean = true,
    val alimentos: List<Alimento> = emptyList(),
    val totalDisponiveis: Int = 0,
    val busca: String = "",
    val categoria: Categoria? = null,
    val somenteFavoritos: Boolean = false,
    val perfil: Perfil? = null,
    val hoje: LocalDate = LocalDate.now(),
    val atualizando: Boolean = false,
    val erro: UiText? = null,
    val mostrarPerfilNecessario: Boolean = false,
    val mensagem: UiText? = null,
) {
    /** FAB "Anunciar" para estabelecimentos e para quem ainda não tem perfil (RF21-CA3). */
    val podeAnunciar: Boolean get() = perfil == null || perfil.isEstabelecimento

    /** Mensagem do estado vazio conforme o filtro ativo (RF06-CA3, RF07-CA2, RF08-CA3). */
    @get:StringRes
    val mensagemVazio: Int
        get() = when {
            somenteFavoritos -> R.string.msg_favoritos_vazio
            totalDisponiveis > 0 -> R.string.msg_busca_vazia
            else -> R.string.msg_lista_vazia
        }
}

private data class Filtros(
    val busca: String = "",
    val categoria: Categoria? = null,
    val somenteFavoritos: Boolean = false,
)

private data class Interacao(
    val atualizando: Boolean = false,
    val mostrarPerfilNecessario: Boolean = false,
    val mensagem: UiText? = null,
)

/** Lista do Início (RF06–RF10). Busca e filtros rodam sobre a lista observada do Room. */
@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModel(
    private val alimentos: AlimentoRepository,
    perfis: PerfilRepository,
    private val clock: Clock,
) : ViewModel() {

    private val filtros = MutableStateFlow(Filtros())
    private val interacao = MutableStateFlow(Interacao())

    /** "Agora" usado no filtro da janela aberta; renovado a cada atualização (RF10). */
    private val agora = MutableStateFlow(clock.agora())

    /** Incrementado em "Tentar novamente" para assinar o banco de novo. */
    private val tentativa = MutableStateFlow(0)

    val uiState: StateFlow<InicioUiState> = tentativa
        .flatMapLatest {
            combine(alimentos.observarDisponiveis(), perfis.perfil, filtros, agora, interacao) { lista, perfil, f, instante, i ->
                montarEstado(lista, perfil, f, instante, i)
            }.catch { erro ->
                emit(InicioUiState(carregando = false, erro = erro.paraUiText()))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())

    fun onBuscaChange(texto: String) = filtros.update { it.copy(busca = texto) }

    fun onCategoriaChange(categoria: Categoria?) = filtros.update { it.copy(categoria = categoria) }

    fun onFavoritosChange(ligado: Boolean) = filtros.update { it.copy(somenteFavoritos = ligado) }

    fun alternarFavorito(id: Long) {
        viewModelScope.launch {
            runCatching { alimentos.alternarFavorito(id) }
                .onFailure { e -> interacao.update { it.copy(mensagem = e.paraUiText()) } }
        }
    }

    /** Expira o que venceu e renova o "agora" (RF10, RN11). [comIndicador] para o puxar-para-atualizar. */
    fun atualizar(comIndicador: Boolean = false) {
        viewModelScope.launch {
            if (comIndicador) interacao.update { it.copy(atualizando = true) }
            runCatching { alimentos.expirarVencidos() }
                .onFailure { e -> interacao.update { it.copy(mensagem = e.paraUiText()) } }
            agora.value = clock.agora()
            interacao.update { it.copy(atualizando = false) }
        }
    }

    fun tentarNovamente() = tentativa.update { it + 1 }

    /** FAB sem perfil abre o diálogo "Perfil necessário" (RF21-CA1). Devolve `true` se pode navegar. */
    fun onAnunciar(): Boolean {
        if (uiState.value.perfil != null) return true
        interacao.update { it.copy(mostrarPerfilNecessario = true) }
        return false
    }

    fun dispensarPerfilNecessario() = interacao.update { it.copy(mostrarPerfilNecessario = false) }

    fun mensagemExibida() = interacao.update { it.copy(mensagem = null) }

    private fun montarEstado(
        lista: List<Alimento>,
        perfil: Perfil?,
        filtros: Filtros,
        instante: LocalDateTime,
        interacao: Interacao,
    ): InicioUiState {
        val disponiveis = lista.filter { RegrasStatus.apareceNoInicio(it, instante) }
        val termo = Formatadores.normalizarBusca(filtros.busca)
        val filtrados = disponiveis.filter { alimento ->
            (termo.isEmpty() || Formatadores.normalizarBusca(alimento.nome).contains(termo)) &&
                (filtros.categoria == null || alimento.categoria == filtros.categoria) &&
                (!filtros.somenteFavoritos || alimento.favorito)
        }
        return InicioUiState(
            carregando = false,
            alimentos = filtrados,
            totalDisponiveis = disponiveis.size,
            busca = filtros.busca,
            categoria = filtros.categoria,
            somenteFavoritos = filtros.somenteFavoritos,
            perfil = perfil,
            hoje = instante.toLocalDate(),
            atualizando = interacao.atualizando,
            mostrarPerfilNecessario = interacao.mostrarPerfilNecessario,
            mensagem = interacao.mensagem,
        )
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                InicioViewModel(container.alimentoRepository, container.perfilRepository, container.clock)
            }
        }
    }
}
