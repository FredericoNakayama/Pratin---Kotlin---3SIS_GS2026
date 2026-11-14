package br.com.fiap.pratin.ui.screens.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.OnboardingRepository
import br.com.fiap.pratin.data.seed.SeedInitializer
import br.com.fiap.pratin.ui.util.appContainer
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class DestinoSplash { ONBOARDING, INICIO }

/**
 * Prepara o app enquanto a marca aparece (RF01): grava o seed na 1ª execução (RN16), expira o que
 * venceu (RN11) e decide o destino pelo onboarding (RN01).
 */
class SplashViewModel(
    private val seed: SeedInitializer,
    private val alimentos: AlimentoRepository,
    private val onboarding: OnboardingRepository,
    private val duracaoMinimaMs: Long = DURACAO_MINIMA_MS,
) : ViewModel() {

    private val _destino = MutableStateFlow<DestinoSplash?>(null)
    val destino: StateFlow<DestinoSplash?> = _destino.asStateFlow()

    init {
        viewModelScope.launch {
            val tempoMinimo = async { delay(duracaoMinimaMs) }
            try {
                seed.executarSeNecessario()
                alimentos.expirarVencidos()
            } catch (e: Exception) {
                // O app continua utilizável mesmo se a preparação falhar; o erro fica no log.
                Log.e(TAG, "Falha ao preparar os dados locais", e)
            }
            val mostrarOnboarding = onboarding.deveMostrar.first()
            tempoMinimo.await()
            _destino.value = if (mostrarOnboarding) DestinoSplash.ONBOARDING else DestinoSplash.INICIO
        }
    }

    companion object {
        private const val TAG = "SplashViewModel"
        const val DURACAO_MINIMA_MS = 1_200L

        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                SplashViewModel(container.seedInitializer, container.alimentoRepository, container.onboardingRepository)
            }
        }
    }
}
