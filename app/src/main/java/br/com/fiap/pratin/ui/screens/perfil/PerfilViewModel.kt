package br.com.fiap.pratin.ui.screens.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.repository.OnboardingRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.domain.error.CampoPerfil
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.TipoPerfil
import br.com.fiap.pratin.domain.rules.CnpjValidator
import br.com.fiap.pratin.domain.rules.PerfilValidator
import br.com.fiap.pratin.ui.util.UiText
import br.com.fiap.pratin.ui.util.appContainer
import br.com.fiap.pratin.ui.util.paraUiText
import br.com.fiap.pratin.ui.util.uiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Campos digitados no formulário de perfil. O CNPJ guarda só dígitos (máscara é visual). */
data class PerfilForm(
    val tipo: TipoPerfil? = null,
    val nome: String = "",
    val cnpj: String = "",
    val endereco: String = "",
)

data class PerfilUiState(
    val carregando: Boolean = true,
    val perfil: Perfil? = null,
    val editando: Boolean = false,
    val form: PerfilForm = PerfilForm(),
    val erros: Map<CampoPerfil, ErroCampo> = emptyMap(),
    val salvando: Boolean = false,
    /** Vira `true` depois de salvar; a tela cheia usa para voltar à origem (RF04-CA7). */
    val salvo: Boolean = false,
    val abrirIntroducao: Boolean = false,
    val mensagem: UiText? = null,
) {
    /** Sem perfil salvo, ou editando: mostra o formulário; senão, o resumo (T06). */
    val mostrarFormulario: Boolean get() = perfil == null || editando
}

/** Perfil local (RF04) e "Ver introdução novamente". */
class PerfilViewModel(
    private val perfis: PerfilRepository,
    private val onboarding: OnboardingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            perfis.perfil.collect { perfil ->
                _uiState.update { it.copy(carregando = false, perfil = perfil) }
            }
        }
    }

    fun iniciarEdicao() = _uiState.update { estado ->
        val perfil = estado.perfil
        val form = perfil?.let { PerfilForm(it.tipo, it.nome, it.cnpj, it.endereco) } ?: estado.form
        estado.copy(editando = true, form = form, erros = emptyMap(), salvo = false)
    }

    fun cancelarEdicao() = _uiState.update { it.copy(editando = false, erros = emptyMap()) }

    fun onTipoChange(tipo: TipoPerfil) = atualizarForm(CampoPerfil.TIPO) { it.copy(tipo = tipo) }

    fun onNomeChange(nome: String) = atualizarForm(CampoPerfil.NOME) { it.copy(nome = nome) }

    /** Aceita colar com pontuação: guarda no máximo 14 dígitos. */
    fun onCnpjChange(texto: String) = atualizarForm(CampoPerfil.CNPJ) {
        it.copy(cnpj = CnpjValidator.apenasDigitos(texto).take(CnpjValidator.TAMANHO))
    }

    fun onEnderecoChange(endereco: String) = atualizarForm(CampoPerfil.ENDERECO) { it.copy(endereco = endereco) }

    /** "Preencher com perfil de demonstração" (RF04-CA8). */
    fun preencherDemonstracao(perfil: Perfil) = _uiState.update {
        it.copy(form = PerfilForm(perfil.tipo, perfil.nome, perfil.cnpj, perfil.endereco), erros = emptyMap())
    }

    fun salvar() {
        val form = _uiState.value.form
        val erros = PerfilValidator.validar(form.tipo, form.nome, form.cnpj, form.endereco)
        if (erros.isNotEmpty()) {
            _uiState.update { it.copy(erros = erros) }
            return
        }
        _uiState.update { it.copy(salvando = true) }
        viewModelScope.launch {
            try {
                perfis.salvar(Perfil(requireNotNull(form.tipo), form.nome, form.cnpj, form.endereco))
                _uiState.update {
                    it.copy(salvando = false, editando = false, salvo = true, mensagem = uiText(R.string.msg_perfil_salvo))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(salvando = false, mensagem = e.paraUiText()) }
            }
        }
    }

    fun verIntroducao() {
        viewModelScope.launch {
            onboarding.reexibir()
            _uiState.update { it.copy(abrirIntroducao = true) }
        }
    }

    fun introducaoAberta() = _uiState.update { it.copy(abrirIntroducao = false) }

    fun mensagemExibida() = _uiState.update { it.copy(mensagem = null) }

    /** Altera o formulário e apaga o erro do campo editado. */
    private fun atualizarForm(campo: CampoPerfil, alteracao: (PerfilForm) -> PerfilForm) = _uiState.update {
        it.copy(form = alteracao(it.form), erros = it.erros - campo)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                PerfilViewModel(container.perfilRepository, container.onboardingRepository)
            }
        }
    }
}
