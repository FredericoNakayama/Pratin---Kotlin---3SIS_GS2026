package br.com.fiap.pratin.di

import android.content.Context
import br.com.fiap.pratin.data.local.db.PratinDatabase
import br.com.fiap.pratin.data.local.prefs.pratinDataStore
import br.com.fiap.pratin.data.repository.AlimentoRepository
import br.com.fiap.pratin.data.repository.DataStoreOnboardingRepository
import br.com.fiap.pratin.data.repository.DataStorePerfilRepository
import br.com.fiap.pratin.data.repository.OnboardingRepository
import br.com.fiap.pratin.data.repository.PerfilRepository
import br.com.fiap.pratin.data.repository.ReservaRepository
import br.com.fiap.pratin.data.repository.RoomAlimentoRepository
import br.com.fiap.pratin.data.repository.RoomReservaRepository
import br.com.fiap.pratin.data.seed.SeedInitializer
import java.time.Clock

/** Dependências do app, criadas uma única vez (injeção manual, ADR-04). */
interface AppContainer {
    val clock: Clock
    val alimentoRepository: AlimentoRepository
    val reservaRepository: ReservaRepository
    val perfilRepository: PerfilRepository
    val onboardingRepository: OnboardingRepository
    val seedInitializer: SeedInitializer
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val appContext = context.applicationContext
    private val database: PratinDatabase by lazy { PratinDatabase.criar(appContext) }

    override val clock: Clock = Clock.systemDefaultZone()
    override val alimentoRepository: AlimentoRepository by lazy { RoomAlimentoRepository(database, clock) }
    override val reservaRepository: ReservaRepository by lazy { RoomReservaRepository(database, clock) }
    override val perfilRepository: PerfilRepository by lazy { DataStorePerfilRepository(appContext.pratinDataStore) }
    override val onboardingRepository: OnboardingRepository by lazy {
        DataStoreOnboardingRepository(appContext.pratinDataStore)
    }
    override val seedInitializer: SeedInitializer by lazy { SeedInitializer(database, clock) }
}
