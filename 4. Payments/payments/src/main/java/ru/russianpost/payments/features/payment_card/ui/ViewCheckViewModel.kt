package ru.russianpost.payments.features.payment_card.ui

import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.entities.AppContextProvider
import javax.inject.Inject

/**
 * ViewModel показа чека
 */
internal class ViewCheckViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    fun onClick() {
        action.value = ViewCheckFragmentDirections.toPaymentDoneDialogAction()
    }
}