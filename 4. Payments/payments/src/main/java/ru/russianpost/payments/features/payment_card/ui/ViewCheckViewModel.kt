package ru.russianpost.payments.features.payment_card.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.entities.AppContextProvider
import javax.inject.Inject

/**
 * ViewModel показа чека
 */
@HiltViewModel
internal class ViewCheckViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    fun onClick() {
        action.value = ViewCheckFragmentDirections.toPaymentDoneDialogAction()
    }
}