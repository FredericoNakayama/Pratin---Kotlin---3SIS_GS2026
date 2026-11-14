package br.com.fiap.pratin.domain.rules

import br.com.fiap.pratin.domain.error.CampoPerfil
import br.com.fiap.pratin.domain.error.ErroCampo
import br.com.fiap.pratin.domain.model.TipoPerfil

/** Regras de um perfil válido (RN17). Mapa vazio = válido. */
object PerfilValidator {
    const val NOME_MINIMO = 3
    const val ENDERECO_MINIMO = 5

    fun validar(
        tipo: TipoPerfil?,
        nome: String,
        cnpj: String,
        endereco: String,
    ): Map<CampoPerfil, ErroCampo> = buildMap {
        if (tipo == null) put(CampoPerfil.TIPO, ErroCampo.TIPO_PERFIL)
        if (nome.trim().length < NOME_MINIMO) put(CampoPerfil.NOME, ErroCampo.NOME_PERFIL)
        if (!CnpjValidator.isValido(cnpj)) put(CampoPerfil.CNPJ, ErroCampo.CNPJ)
        if (endereco.trim().length < ENDERECO_MINIMO) put(CampoPerfil.ENDERECO, ErroCampo.ENDERECO)
    }
}
