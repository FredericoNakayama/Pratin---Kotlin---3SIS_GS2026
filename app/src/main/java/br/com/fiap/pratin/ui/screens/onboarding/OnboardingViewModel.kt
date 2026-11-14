package br.com.fiap.pratin.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.data.repository.OnboardingRepository
import br.com.fiap.pratin.ui.util.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val naoMostrarNovamente: Boolean = false,
    val concluido: Boolean = false,
)

/** Introdução em 3 páginas (RF02). */
class OnboardingViewModel(
    private val onboarding: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onNaoMostrarChange(valor: Boolean) = _uiState.update { it.copy(naoMostrarNovamente = valor) }

    /** "Começar": grava as preferências e libera a navegação para o Início (RF02-CA3). */
    fun concluir() {
        viewModelScope.launch {
            onboarding.concluir(_uiState.value.naoMostrarNovamente)
            _uiState.update { it.copy(concluido = true) }
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { OnboardingViewModel(appContainer().onboardingRepository) }
        }
    }
}
