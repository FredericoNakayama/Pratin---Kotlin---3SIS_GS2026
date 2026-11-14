package br.com.fiap.pratin.domain.rules

/** Validação de CNPJ pelo cálculo dos dígitos verificadores (RN02). Não consulta a Receita. */
object CnpjValidator {
    const val TAMANHO = 14
    private val PESOS_PRIMEIRO_DV = intArrayOf(5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)
    private val PESOS_SEGUNDO_DV = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    /** Remove tudo o que não for dígito. */
    fun apenasDigitos(valor: String): String = valor.filter(Char::isDigit)

    fun isValido(valor: String?): Boolean {
        if (valor == null) return false
        val cnpj = apenasDigitos(valor)
        if (cnpj.length != TAMANHO) return false
        if (cnpj.all { it == cnpj.first() }) return false

        val digitos = cnpj.map { it.digitToInt() }
        val primeiro = calcularDigito(digitos, PESOS_PRIMEIRO_DV)
        val segundo = calcularDigito(digitos, PESOS_SEGUNDO_DV)
        return digitos[12] == primeiro && digitos[13] == segundo
    }

    /** Soma ponderada dos primeiros dígitos; `resto < 2 → 0`, senão `11 - resto`. */
    private fun calcularDigito(digitos: List<Int>, pesos: IntArray): Int {
        val soma = pesos.indices.sumOf { digitos[it] * pesos[it] }
        val resto = soma % 11
        return if (resto < 2) 0 else 11 - resto
    }
}
