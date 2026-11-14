package br.com.fiap.pratin.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import br.com.fiap.pratin.data.local.prefs.ChavesPreferencias
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Preferências da introdução (RN01). */
interface OnboardingRepository {
    /** `true` = a Splash deve abrir o Onboarding. */
    val deveMostrar: Flow<Boolean>

    /** Chamado no "Começar": grava que viu e o valor de "Não mostrar novamente". */
    suspend fun concluir(naoMostrarNovamente: Boolean)

    /** "Ver introdução novamente" no Perfil. */
    suspend fun reexibir()
}

class DataStoreOnboardingRepository(
    private val dataStore: DataStore<Preferences>,
) : OnboardingRepository {

    override val deveMostrar: Flow<Boolean> = dataStore.data.map { prefs ->
        val visto = prefs[ChavesPreferencias.ONBOARDING_VISTO] ?: false
        val naoMostrar = prefs[ChavesPreferencias.ONBOARDING_NAO_MOSTRAR] ?: false
        !(visto && naoMostrar)
    }

    override suspend fun concluir(naoMostrarNovamente: Boolean) {
        dataStore.edit { prefs ->
            prefs[ChavesPreferencias.ONBOARDING_VISTO] = true
            prefs[ChavesPreferencias.ONBOARDING_NAO_MOSTRAR] = naoMostrarNovamente
        }
    }

    override suspend fun reexibir() {
        dataStore.edit { prefs -> prefs[ChavesPreferencias.ONBOARDING_NAO_MOSTRAR] = false }
    }
}
