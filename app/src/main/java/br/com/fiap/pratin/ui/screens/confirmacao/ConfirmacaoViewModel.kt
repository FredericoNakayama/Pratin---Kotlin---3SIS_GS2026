package br.com.fiap.pratin.ui.screens.confirmacao

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.toRoute
import br.com.fiap.pratin.data.repository.ReservaRepository
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.ui.navigation.ConfirmacaoReservaRoute
import br.com.fiap.pratin.ui.util.appContainer
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ConfirmacaoUiState(
    val carregando: Boolean = true,
    val detalhe: ReservaDetalhada? = null,
)

/** Confirmação da reserva com o código de retirada (RF14). */
class ConfirmacaoViewModel(
    savedStateHandle: SavedStateHandle,
    reservas: ReservaRepository,
) : ViewModel() {

    private val reservaId = savedStateHandle.toRoute<ConfirmacaoReservaRoute>().reservaId

    val uiState: StateFlow<ConfirmacaoUiState> = reservas.observarPorId(reservaId)
        .map { ConfirmacaoUiState(carregando = false, detalhe = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ConfirmacaoUiState())

    companion object {
        val Factory = viewModelFactory {
            initializer { ConfirmacaoViewModel(createSavedStateHandle(), appContainer().reservaRepository) }
        }
    }
}
