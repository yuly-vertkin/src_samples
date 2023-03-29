package ru.russianpost.payments.base.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.entities.AppContextProvider

internal class SimpleWebViewViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    val url = MutableLiveData<String?>(null)

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<SimpleWebViewViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): SimpleWebViewViewModel
    }

    override fun onCreate() {
        super.onCreate()

        url.value = savedStateHandle.get<String>(FRAGMENT_PARAMS_NAME)
    }
}