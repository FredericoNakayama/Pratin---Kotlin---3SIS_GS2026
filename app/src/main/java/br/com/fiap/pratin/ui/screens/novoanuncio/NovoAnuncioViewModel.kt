package br.com.fiap.pratin.ui.screens.novoanuncio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.domain.error.CampoAnuncio
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.Categoria
import br.com.fiap.pratin.domain.model.NovoAnuncio
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.Unidade
import br.com.fiap.pratin.domain.rules.AnuncioValidator
import br.com.fiap.pratin.domain.rules.agora
import br.com.fiap.pratin.domain.rules.hoje
import br.com.fiap.pratin.ui.util.UiText
import br.com.fiap.pratin.ui.util.appContainer
import br.com.fiap.pratin.ui.util.paraUiText
import br.com.fiap.pratin.ui.util.uiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import java.time.LocalDateTime

data class NovoAnuncioUiState(
    val form: NovoAnuncio = NovoAnuncio(),
    val erros: Map<CampoAnuncio, ErroCampo> = emptyMap(),
    val perfil: Perfil? = null,
    val hoje: LocalDate = LocalDate.now(),
    /** "Agora" do relógio injetado, usado para sugerir os horários de retirada. */
    val agora: LocalDateTime = LocalDateTime.now(),
    val publicando: Boolean = false,
    /** Preenchido após gravar; a tela volta ao Início (RF12-CA3). */
    val publicadoId: Long? = null,
    val mensagem: UiText? = null,
) {
    /** Último dia selecionável na validade (RN03). */
    val ultimoDiaValidade: LocalDate get() = hoje.plusDays(AnuncioValidator.DIAS_MAXIMOS_VALIDADE)

    /** A retirada termina até o dia da validade; sem validade, até o limite de 7 dias. */
    val ultimoDiaRetirada: LocalDate get() = form.dataValidade ?: ultimoDiaValidade
}

/**
 * Formulário de novo anúncio (RF12). O estado fica no ViewModel para sobreviver à rotação
 * (RF12-CA5) e a validação usa o AnuncioValidator do domínio, nunca o Composable.
 */
class NovoAnuncioViewModel(
    perfis: PerfilRepository,
    private val alimentos: AlimentoRepository,
    private val clock: Clock,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovoAnuncioUiState(hoje = clock.hoje(), agora = clock.agora()))
    val uiState: StateFlow<NovoAnuncioUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            perfis.perfil.collect { perfil ->
                _uiState.update { estado ->
                    // Endereço vem preenchido com o do perfil, sem sobrescrever o que já foi digitado.
                    val form = if (estado.form.endereco.isBlank() && perfil != null) {
                        estado.form.copy(endereco = perfil.endereco)
                    } else {
                        estado.form
                    }
                    estado.copy(perfil = perfil, form = form)
                }
            }
        }
    }

    fun onNomeChange(valor: String) =
        atualizar(CampoAnuncio.NOME) { it.copy(nome = valor.take(AnuncioValidator.NOME_MAXIMO)) }

    fun onCategoriaChange(valor: Categoria) = atualizar(CampoAnuncio.CATEGORIA) { it.copy(categoria = valor) }

    fun onQuantidadeChange(valor: String) =
        atualizar(CampoAnuncio.QUANTIDADE) { it.copy(quantidadeTexto = valor.filter { c -> c.isDigit() || c == ',' || c == '.' }) }

    fun onUnidadeChange(valor: Unidade) = atualizar(CampoAnuncio.UNIDADE) { it.copy(unidade = valor) }

    fun onValidadeChange(valor: LocalDate) = atualizar(CampoAnuncio.VALIDADE) { it.copy(dataValidade = valor) }

    fun onRetiradaInicioChange(valor: LocalDateTime) =
        atualizar(CampoAnuncio.JANELA) { it.copy(retiradaInicio = valor) }

    fun onRetiradaFimChange(valor: LocalDateTime) = atualizar(CampoAnuncio.JANELA) { it.copy(retiradaFim = valor) }

    fun onEnderecoChange(valor: String) = atualizar(CampoAnuncio.ENDERECO) { it.copy(endereco = valor) }

    fun onFotoUrlChange(valor: String) = atualizar(CampoAnuncio.FOTO_URL) { it.copy(fotoUrl = valor.trim()) }

    fun onDeclaracaoChange(valor: Boolean) = atualizar(CampoAnuncio.DECLARACAO) { it.copy(declaracaoProprio = valor) }

    /** Valida tudo; com erro, mostra as mensagens e não grava nada (RF12-CA2). */
    fun publicar() {
        val estado = _uiState.value
        if (estado.publicando) return
        val perfil = estado.perfil ?: return
        val erros = AnuncioValidator.validar(estado.form, clock.agora())
        if (erros.isNotEmpty()) {
            _uiState.update { it.copy(erros = erros, mensagem = uiText(R.string.erro_anuncio_invalido)) }
            return
        }
        _uiState.update { it.copy(publicando = true) }
        viewModelScope.launch {
            try {
                val id = alimentos.publicar(estado.form, perfil)
                _uiState.update { it.copy(publicando = false, publicadoId = id) }
            } catch (e: Exception) {
                _uiState.update { it.copy(publicando = false, mensagem = e.paraUiText()) }
            }
        }
    }

    fun mensagemExibida() = _uiState.update { it.copy(mensagem = null) }

    /** Altera o formulário e apaga o erro do campo editado. */
    private fun atualizar(campo: CampoAnuncio, alteracao: (NovoAnuncio) -> NovoAnuncio) = _uiState.update {
        it.copy(form = alteracao(it.form), erros = it.erros - campo)
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                NovoAnuncioViewModel(container.perfilRepository, container.alimentoRepository, container.clock)
            }
        }
    }
}
