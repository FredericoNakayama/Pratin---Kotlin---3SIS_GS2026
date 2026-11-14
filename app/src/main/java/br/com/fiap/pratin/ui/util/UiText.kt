package br.com.fiap.pratin.ui.util

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import br.com.fiap.pratin.R
import br.com.fiap.pratin.domain.error.ErroRegra
import br.com.fiap.pratin.domain.error.RegraNegocioException

/** Texto que o ViewModel entrega à UI sem depender de Context. */
sealed interface UiText {
    data class Res(@StringRes val id: Int, val args: List<Any> = emptyList()) : UiText
    data class Dinamico(val texto: String) : UiText
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Res -> stringResource(id, *args.toTypedArray())
    is UiText.Dinamico -> texto
}

fun uiText(@StringRes id: Int, vararg args: Any): UiText = UiText.Res(id, args.toList())

/** Converte qualquer falha em mensagem: regra de negócio vira o texto dela; o resto, erro genérico. */
fun Throwable.paraUiText(): UiText = when (this) {
    is RegraNegocioException -> UiText.Res(erro.mensagemRes())
    else -> UiText.Res(R.string.msg_erro_inesperado)
}

@StringRes
fun ErroRegra.mensagemRes(): Int = when (this) {
    ErroRegra.ALIMENTO_NAO_ENCONTRADO -> R.string.erro_alimento_nao_encontrado
    ErroRegra.JA_RESERVADO -> R.string.erro_ja_reservado
    ErroRegra.PRAZO_ENCERRADO -> R.string.erro_prazo_encerrado
    ErroRegra.CODIGO_INVALIDO -> R.string.erro_codigo_invalido
    ErroRegra.CODIGO_TAMANHO -> R.string.erro_codigo_tamanho
    ErroRegra.APENAS_ONGS -> R.string.erro_apenas_ongs
    ErroRegra.APENAS_ESTABELECIMENTOS -> R.string.erro_apenas_estabelecimentos
    ErroRegra.NAO_PODE_CANCELAR_ANUNCIO -> R.string.erro_nao_pode_cancelar_anuncio
    ErroRegra.NAO_PODE_CANCELAR_RESERVA -> R.string.erro_nao_pode_cancelar_reserva
    ErroRegra.RETIRADA_INDISPONIVEL -> R.string.erro_retirada_indisponivel
    ErroRegra.RESERVA_NAO_ENCONTRADA -> R.string.erro_reserva_nao_encontrada
    ErroRegra.NAO_E_DONO -> R.string.erro_nao_e_dono
    ErroRegra.ANUNCIO_INVALIDO -> R.string.erro_anuncio_invalido
    ErroRegra.PERFIL_NECESSARIO -> R.string.msg_perfil_necessario
}
