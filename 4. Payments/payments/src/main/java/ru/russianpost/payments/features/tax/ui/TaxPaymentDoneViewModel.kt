package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneViewModel
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import javax.inject.Inject

/**
 * ViewModel результатов платежа
 */
@HiltViewModel
internal class TaxPaymentDoneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: TaxDetailsRepository,
    private val cardRepository: PaymentCardRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : PaymentDoneViewModel(savedStateHandle, cardRepository, appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        var taxDetails = repository.getData()
        val params = paramsRepository.getData()
        if(params.id.isNotEmpty()) {
            viewModelScope.launch {
                repository.getTaxDetails(params.id).collect {
                    when (it) {
                        is Response.Success -> taxDetails = it.data
                        is Response.Error -> println("Error")
                        else -> {}
                    }
                }
            }
        }
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
            btnLabel.value = getString(R.string.ps_back_main)
        }
    }

    override fun viewCheckAction(arg: String) {
        action.value = TaxPaymentDoneFragmentDirections.toViewCheckAction(arg)
    }
}