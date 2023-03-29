package ru.russianpost.payments.features.tax.ui

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
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository

/**
 * ViewModel результатов платежа
 */
internal class TaxPaymentDoneViewModel @AssistedInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    private val repository: TaxDetailsRepository,
    cardRepository: PaymentCardRepository,
    paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : PaymentDoneViewModel(savedStateHandle, cardRepository, paramsRepository, appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<TaxPaymentDoneViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): TaxPaymentDoneViewModel
    }

    override fun onCreate() {
        super.onCreate()

        var taxDetails = repository.getData()
        val paymentId = getAndClearIdParam()

        paymentId?.let {
            processNetworkCall(
                action = { repository.getTaxDetails(it) },
                onSuccess = { taxDetails = it },
                onError = { showServiceUnavailableDialog() },
            )
        } ?: showServiceUnavailableDialog()

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    id = R.id.ps_sender,
                    title = getString(R.string.ps_sender),
                    subtitle = MutableLiveData(getString(R.string.ps_full_name_template,
                        taxDetails.lastName, taxDetails.firstName, taxDetails.patronymic)),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_recipient,
                    title = getString(R.string.ps_recipient),
                    subtitle = MutableLiveData(taxDetails.bankName),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_sum,
                    title = getString(R.string.ps_payment_sum),
                    subtitle = MutableLiveData(getString(R.string.ps_sum_str,
                        taxDetails.sum, rubSign)),
                    isValueCell = true,
                ),
            ))
        }
    }

    override fun viewCheckAction(arg: String) {
        action.value = TaxPaymentDoneFragmentDirections.toViewCheckAction(arg)
    }
}