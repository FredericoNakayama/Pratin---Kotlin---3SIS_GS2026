package br.com.fiap.pratin.domain.error

/** Violações de regra de negócio. A UI converte cada uma em um texto de strings.xml. */
enum class ErroRegra {
    ALIMENTO_NAO_ENCONTRADO,
    JA_RESERVADO,
    PRAZO_ENCERRADO,
    CODIGO_INVALIDO,
    CODIGO_TAMANHO,
    APENAS_ONGS,
    APENAS_ESTABELECIMENTOS,
    NAO_PODE_CANCELAR_ANUNCIO,
    NAO_PODE_CANCELAR_RESERVA,
    RETIRADA_INDISPONIVEL,
    RESERVA_NAO_ENCONTRADA,
    NAO_E_DONO,
    ANUNCIO_INVALIDO,
    PERFIL_NECESSARIO,
}

/** Lançada pelos repositórios quando uma regra impede a operação. Nada é gravado. */
class RegraNegocioException(val erro: ErroRegra) : Exception(erro.name)

/** Erros de validação de campo de formulário. */
enum class ErroCampo {
    NOME_ALIMENTO,
    CATEGORIA,
    QUANTIDADE,
    UNIDADE,
    VALIDADE_OBRIGATORIA,
    VALIDADE_FORA_DO_PRAZO,
    JANELA_OBRIGATORIA,
    JANELA_NO_PASSADO,
    JANELA_CURTA,
    JANELA_APOS_VALIDADE,
    ENDERECO,
    FOTO_URL,
    DECLARACAO,
    TIPO_PERFIL,
    NOME_PERFIL,
    CNPJ,
}

/** Campos do formulário de anúncio (chaves do mapa de erros). */
enum class CampoAnuncio { NOME, CATEGORIA, QUANTIDADE, UNIDADE, VALIDADE, JANELA, ENDERECO, FOTO_URL, DECLARACAO }

/** Campos do formulário de perfil (chaves do mapa de erros). */
enum class CampoPerfil { TIPO, NOME, CNPJ, ENDERECO }
