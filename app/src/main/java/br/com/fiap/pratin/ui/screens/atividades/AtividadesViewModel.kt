package br.com.fiap.pratin.ui.screens.atividades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.fiap.pratin.R
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.data.repository.ReservaRepository
import br.com.fiap.pratin.domain.error.ErroRegra
import br.com.fiap.pratin.domain.error.RegraNegocioException
import br.com.fiap.pratin.domain.model.AnuncioDetalhado
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.ReservaDetalhada
import br.com.fiap.pratin.domain.rules.CodigoGenerator
import br.com.fiap.pratin.domain.rules.agora
import br.com.fiap.pratin.ui.util.UiText
import br.com.fiap.pratin.ui.util.appContainer
import br.com.fiap.pratin.ui.util.paraUiText
import br.com.fiap.pratin.ui.util.uiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDateTime

/** Diálogo aberto em Atividades. */
sealed interface DialogoAtividade {
    data class CancelarReserva(val reservaId: Long) : DialogoAtividade
    data class CancelarAnuncio(val alimentoId: Long) : DialogoAtividade
    data class ConfirmarRetirada(val alimentoId: Long) : DialogoAtividade
}

data class AtividadesUiState(
    val carregando: Boolean = true,
    val perfil: Perfil? = null,
    val reservas: List<ReservaDetalhada> = emptyList(),
    val anuncios: List<AnuncioDetalhado> = emptyList(),
    val agora: LocalDateTime = LocalDateTime.now(),
    val atualizando: Boolean = false,
    val dialogo: DialogoAtividade? = null,
    val codigoDigitado: String = "",
    val erroCodigo: UiText? = null,
    val processando: Boolean = false,
    val mensagem: UiText? = null,
)

/** Listas carregadas conforme o perfil: reservas (ONG) ou anúncios (estabelecimento). */
private data class Listas(
    val perfil: Perfil? = null,
    val reservas: List<ReservaDetalhada> = emptyList(),
    val anuncios: List<AnuncioDetalhado> = emptyList(),
)

private data class Interacao(
    val atualizando: Boolean = false,
    val dialogo: DialogoAtividade? = null,
    val codigoDigitado: String = "",
    val erroCodigo: UiText? = null,
    val processando: Boolean = false,
    val mensagem: UiText? = null,
)

/** Minhas atividades (RF15–RF20). */
@OptIn(ExperimentalCoroutinesApi::class)
class AtividadesViewModel(
    perfis: PerfilRepository,
    private val alimentos: AlimentoRepository,
    private val reservas: ReservaRepository,
    private val clock: Clock,
) : ViewModel() {

    private val interacao = MutableStateFlow(Interacao())
    private val agora = MutableStateFlow(clock.agora())

    private val listas: Flow<Listas> = perfis.perfil.flatMapLatest { perfil ->
        when {
            perfil == null -> flowOf(Listas())
            perfil.isOng -> reservas.observarDaOng(perfil.cnpj).map { Listas(perfil, reservas = it) }
            else -> alimentos.observarAnuncios(perfil.cnpj).map { Listas(perfil, anuncios = it) }
        }
    }

    val uiState: StateFlow<AtividadesUiState> =
        combine(listas, agora, interacao) { l, instante, i ->
            AtividadesUiState(
                carregando = false,
                perfil = l.perfil,
                reservas = l.reservas,
                anuncios = l.anuncios,
                agora = instante,
                atualizando = i.atualizando,
                dialogo = i.dialogo,
                codigoDigitado = i.codigoDigitado,
                erroCodigo = i.erroCodigo,
                processando = i.processando,
                mensagem = i.mensagem,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AtividadesUiState())

    /** Expira o que venceu e renova o "agora" (RF10, RF20). */
    fun atualizar(comIndicador: Boolean = false) {
        viewModelScope.launch {
            if (comIndicador) interacao.update { it.copy(atualizando = true) }
            runCatching { alimentos.expirarVencidos() }
                .onFailure { e -> interacao.update { it.copy(mensagem = e.paraUiText()) } }
            agora.value = clock.agora()
            interacao.update { it.copy(atualizando = false) }
        }
    }

    fun abrirDialogo(dialogo: DialogoAtividade) =
        interacao.update { it.copy(dialogo = dialogo, codigoDigitado = "", erroCodigo = null) }

    fun fecharDialogo() = interacao.update { it.copy(dialogo = null, codigoDigitado = "", erroCodigo = null) }

    /** Só letras e números, em maiúsculas, até 6 caracteres (RF19-CA1). */
    fun onCodigoChange(texto: String) = interacao.update {
        it.copy(
            codigoDigitado = texto.filter(Char::isLetterOrDigit).uppercase().take(CodigoGenerator.TAMANHO),
            erroCodigo = null,
        )
    }

    fun confirmarDialogo() {
        val dialogo = interacao.value.dialogo ?: return
        val perfil = uiState.value.perfil ?: return
        interacao.update { it.copy(processando = true) }
        viewModelScope.launch {
            try {
                val sucesso = when (dialogo) {
                    is DialogoAtividade.CancelarReserva -> {
                        reservas.cancelar(dialogo.reservaId, perfil)
                        R.string.msg_reserva_cancelada
                    }
                    is DialogoAtividade.CancelarAnuncio -> {
                        alimentos.cancelarAnuncio(dialogo.alimentoId, perfil)
                        R.string.msg_anuncio_cancelado
                    }
                    is DialogoAtividade.ConfirmarRetirada -> {
                        reservas.confirmarRetirada(dialogo.alimentoId, interacao.value.codigoDigitado, perfil)
                        R.string.msg_retirada_confirmada
                    }
                }
                interacao.update {
                    it.copy(processando = false, dialogo = null, codigoDigitado = "", mensagem = uiText(sucesso))
                }
            } catch (e: RegraNegocioException) {
                // Código errado fica no próprio diálogo e nada muda (RF19-CA3).
                if (e.erro == ErroRegra.CODIGO_INVALIDO || e.erro == ErroRegra.CODIGO_TAMANHO) {
                    interacao.update { it.copy(processando = false, erroCodigo = e.paraUiText()) }
                } else {
                    interacao.update { it.copy(processando = false, dialogo = null, mensagem = e.paraUiText()) }
                }
            } catch (e: Exception) {
                interacao.update { it.copy(processando = false, dialogo = null, mensagem = e.paraUiText()) }
            }
        }
    }

    fun mensagemExibida() = interacao.update { it.copy(mensagem = null) }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                AtividadesViewModel(
                    container.perfilRepository,
                    container.alimentoRepository,
                    container.reservaRepository,
                    container.clock,
                )
            }
        }
    }
}
