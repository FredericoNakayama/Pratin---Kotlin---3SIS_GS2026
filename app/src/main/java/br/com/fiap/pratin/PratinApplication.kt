package br.com.fiap.pratin

import android.app.Application
import br.com.fiap.pratin.di.AppContainer
import br.com.fiap.pratin.di.DefaultAppContainer

/** Cria o [AppContainer] usado pelas factories dos ViewModels. */
class PratinApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
