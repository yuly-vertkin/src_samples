package ru.russianpost.payments.base.ui

import android.content.Intent
import androidx.lifecycle.MutableLiveData
import ru.russianpost.payments.entities.AppContextProvider

internal abstract class BaseStartActivityViewModel(appContextProvider: AppContextProvider) : BaseMenuViewModel(appContextProvider) {
    val startIntent = MutableLiveData<Intent>()

    abstract fun onActivityResult(intent: Intent?)
}