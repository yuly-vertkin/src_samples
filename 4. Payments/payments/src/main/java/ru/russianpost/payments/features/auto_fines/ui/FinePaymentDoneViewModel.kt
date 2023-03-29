package ru.russianpost.payments.features.auto_fines.ui

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
internal class FinePaymentDoneViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    cardRepository: PaymentCardRepository,
    paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : PaymentDoneViewModel(savedStateHandle, cardRepository, paramsRepository, appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FinePaymentDoneViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FinePaymentDoneViewModel
    }

    override fun onCreate() {
        super.onCreate()

        val data = cardRepository.getData()

        with(context.resources) {
            addFields(
                listOf(
                    CellFieldValue(
                        id = R.id.ps_payment_purpose,
                        title = getString(R.string.ps_payment_purpose),
                        subtitle = MutableLiveData(data.description),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        id = R.id.ps_paid,
                        title = getString(R.string.ps_paid),
                        subtitle = MutableLiveData(makeSum(data.amount)),
                        isValueCell = true,
                    ),
                )
            )
        }
    }

    override fun viewCheckAction(arg: String) {
        action.value = FinePaymentDoneFragmentDirections.toViewCheckAction(arg)
    }
}