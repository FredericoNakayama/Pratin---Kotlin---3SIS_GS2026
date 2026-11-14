package br.com.fiap.pratin.ui.screens.detalhes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.data.repository.ReservaRepository
import br.com.fiap.pratin.domain.model.Alimento
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.rules.RegrasStatus
import br.com.fiap.pratin.domain.rules.agora
import br.com.fiap.pratin.ui.navigation.DetalhesRoute
import br.com.fiap.pratin.ui.util.UiText
import br.com.fiap.pratin.ui.util.appContainer
import br.com.fiap.pratin.ui.util.paraUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate

/** O que o rodapé de Detalhes mostra, conforme o perfil e o status (RF11-CA4/CA5). */
enum class AcaoDetalhes { RESERVAR, APENAS_ONGS, INDISPONIVEL }

data class DetalhesUiState(
    val carregando: Boolean = true,
    val alimento: Alimento? = null,
    val perfil: Perfil? = null,
    val hoje: LocalDate = LocalDate.now(),
    val acao: AcaoDetalhes = AcaoDetalhes.INDISPONIVEL,
    val reservando: Boolean = false,
    val confirmarReserva: Boolean = false,
    val mostrarPerfilNecessario: Boolean = false,
    /** Preenchido após reservar; a tela navega para a Confirmação (RF13-CA2). */
    val reservaCriadaId: Long? = null,
    val mensagem: UiText? = null,
)

private data class Interacao(
    val reservando: Boolean = false,
    val confirmarReserva: Boolean = false,
    val mostrarPerfilNecessario: Boolean = false,
    val reservaCriadaId: Long? = null,
    val mensagem: UiText? = null,
)

/** Detalhes de um alimento e a reserva pela ONG (RF11, RF13). */
class DetalhesViewModel(
    savedStateHandle: SavedStateHandle,
    private val alimentos: AlimentoRepository,
    private val reservas: ReservaRepository,
    perfis: PerfilRepository,
    private val clock: Clock,
) : ViewModel() {

    private val alimentoId = savedStateHandle.toRoute<DetalhesRoute>().alimentoId
    private val interacao = MutableStateFlow(Interacao())

    val uiState: StateFlow<DetalhesUiState> =
        combine(alimentos.observarPorId(alimentoId), perfis.perfil, interacao) { alimento, perfil, i ->
            val agora = clock.agora()
            DetalhesUiState(
                carregando = false,
                alimento = alimento,
                perfil = perfil,
                hoje = agora.toLocalDate(),
                acao = when {
                    alimento == null || !RegrasStatus.podeReservar(alimento, agora) -> AcaoDetalhes.INDISPONIVEL
                    perfil?.isEstabelecimento == true -> AcaoDetalhes.APENAS_ONGS
                    else -> AcaoDetalhes.RESERVAR
                },
                reservando = i.reservando,
                confirmarReserva = i.confirmarReserva,
                mostrarPerfilNecessario = i.mostrarPerfilNecessario,
                reservaCriadaId = i.reservaCriadaId,
                mensagem = i.mensagem,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetalhesUiState())

    fun alternarFavorito() {
        viewModelScope.launch {
            runCatching { alimentos.alternarFavorito(alimentoId) }
                .onFailure { e -> interacao.update { it.copy(mensagem = e.paraUiText()) } }
        }
    }

    /** Sem perfil: diálogo "Perfil necessário". ONG: pede confirmação (RF13-CA1). */
    fun onReservar() = interacao.update {
        if (uiState.value.perfil == null) it.copy(mostrarPerfilNecessario = true) else it.copy(confirmarReserva = true)
    }

    fun dispensarDialogos() = interacao.update { it.copy(confirmarReserva = false, mostrarPerfilNecessario = false) }

    fun confirmarReserva() {
        val perfil = uiState.value.perfil ?: return
        interacao.update { it.copy(confirmarReserva = false, reservando = true) }
        viewModelScope.launch {
            try {
                val reservaId = reservas.reservar(alimentoId, perfil)
                interacao.update { it.copy(reservando = false, reservaCriadaId = reservaId) }
            } catch (e: Exception) {
                interacao.update { it.copy(reservando = false, mensagem = e.paraUiText()) }
            }
        }
    }

    fun navegouParaConfirmacao() = interacao.update { it.copy(reservaCriadaId = null) }

    fun mensagemExibida() = interacao.update { it.copy(mensagem = null) }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                DetalhesViewModel(
                    createSavedStateHandle(),
                    container.alimentoRepository,
                    container.reservaRepository,
                    container.perfilRepository,
                    container.clock,
                )
            }
        }
    }
}
