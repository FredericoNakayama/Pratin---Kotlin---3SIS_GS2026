package br.com.fiap.pratin.ui.util

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import br.com.fiap.pratin.PratinApplication
import br.com.fiap.pratin.di.AppContainer

/** Acesso ao [AppContainer] dentro dos `initializer { }` das factories de ViewModel. */
fun CreationExtras.appContainer(): AppContainer =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PratinApplication).container
