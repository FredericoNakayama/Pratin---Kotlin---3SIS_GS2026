package br.com.fiap.pratin.data.local.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

/** Única instância do DataStore do app (spec/03-arquitetura.md §5.4). */
val Context.pratinDataStore: DataStore<Preferences> by preferencesDataStore(name = "pratin_prefs")

/** Chaves gravadas no DataStore. */
object ChavesPreferencias {
    val ONBOARDING_VISTO = booleanPreferencesKey("onboarding_visto")
    val ONBOARDING_NAO_MOSTRAR = booleanPreferencesKey("onboarding_nao_mostrar")
    val PERFIL_TIPO = stringPreferencesKey("perfil_tipo")
    val PERFIL_NOME = stringPreferencesKey("perfil_nome")
    val PERFIL_CNPJ = stringPreferencesKey("perfil_cnpj")
    val PERFIL_ENDERECO = stringPreferencesKey("perfil_endereco")
}
