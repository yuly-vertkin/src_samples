package ru.russianpost.payments.base.ui

import ru.russianpost.payments.entities.AppContextProvider

internal abstract class BaseMenuViewModel(appContextProvider: AppContextProvider) : BaseViewModel(appContextProvider) {
    open fun onMenuItem1() {}
}