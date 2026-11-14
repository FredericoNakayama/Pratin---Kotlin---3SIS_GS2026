package br.com.fiap.pratin.domain.model

/** Perfil salvo no aparelho. Não há login: o CNPJ (só dígitos) identifica o dono (RN04). */
data class Perfil(
    val tipo: TipoPerfil,
    val nome: String,
    val cnpj: String,
    val endereco: String,
) {
    val isOng: Boolean get() = tipo == TipoPerfil.ONG
    val isEstabelecimento: Boolean get() = tipo == TipoPerfil.ESTABELECIMENTO
}
