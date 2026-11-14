package br.com.fiap.pratin.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import br.com.fiap.pratin.data.local.prefs.ChavesPreferencias
import br.com.fiap.pratin.domain.model.Perfil
import br.com.fiap.pratin.domain.model.TipoPerfil
import br.com.fiap.pratin.domain.rules.CnpjValidator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Perfil salvo no aparelho (ADR-02). */
interface PerfilRepository {
    /** `null` enquanto o usuário não configurou o perfil. */
    val perfil: Flow<Perfil?>

    suspend fun salvar(perfil: Perfil)
}

class DataStorePerfilRepository(
    private val dataStore: DataStore<Preferences>,
) : PerfilRepository {

    override val perfil: Flow<Perfil?> = dataStore.data
        .map { prefs ->
            val tipo = prefs[ChavesPreferencias.PERFIL_TIPO]
                ?.let { codigo -> TipoPerfil.entries.firstOrNull { it.name == codigo } }
            val nome = prefs[ChavesPreferencias.PERFIL_NOME]
            val cnpj = prefs[ChavesPreferencias.PERFIL_CNPJ]
            val endereco = prefs[ChavesPreferencias.PERFIL_ENDERECO]
            if (tipo == null || nome == null || cnpj == null || endereco == null) {
                null
            } else {
                Perfil(tipo = tipo, nome = nome, cnpj = cnpj, endereco = endereco)
            }
        }
        .distinctUntilChanged()

    override suspend fun salvar(perfil: Perfil) {
        dataStore.edit { prefs ->
            prefs[ChavesPreferencias.PERFIL_TIPO] = perfil.tipo.name
            prefs[ChavesPreferencias.PERFIL_NOME] = perfil.nome.trim()
            prefs[ChavesPreferencias.PERFIL_CNPJ] = CnpjValidator.apenasDigitos(perfil.cnpj)
            prefs[ChavesPreferencias.PERFIL_ENDERECO] = perfil.endereco.trim()
        }
    }
}
