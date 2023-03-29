package ru.russianpost.payments.features.uid_tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneViewModel

/**
 * ViewModel результатов платежа
 */
internal class UidTaxPaymentDoneViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    cardRepository: PaymentCardRepository,
    paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : PaymentDoneViewModel(savedStateHandle, cardRepository, paramsRepository, appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<UidTaxPaymentDoneViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): UidTaxPaymentDoneViewModel
    }

    override fun onCreate() {
        super.onCreate()

        val data = cardRepository.getData()

        with(context.resources) {
            addFields(
                listOf(
                    CellFieldValue(
                        title = getString(R.string.ps_tax_payment),
                        subtitle = MutableLiveData(data.description),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sum),
                        subtitle = MutableLiveData(makeSum(cardRepository.getData().amount)),
                        isValueCell = true,
                    ),
                )
            )
        }
    }

    override fun viewCheckAction(arg: String) {
        action.value = UidTaxPaymentDoneFragmentDirections.viewCheckAction(arg)
    }
}