package ru.russianpost.payments.features.auto_fines.ui

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
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneViewModel
import javax.inject.Inject

/**
 * ViewModel результатов платежа
 */
@HiltViewModel
internal class FinePaymentDoneViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val cardRepository: PaymentCardRepository,
    private val repository: AutoFinesRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : PaymentDoneViewModel(savedStateHandle, cardRepository, appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val params = paramsRepository.getData()
        val fineId = if(params.id.isNotEmpty()) params.id
                     else repository.getData().currentFineId

        viewModelScope.launch {
            repository.getFine(fineId).collect {
                isLoading.value = it is Response.Loading
                when(it) {
                    is Response.Success ->
                        with(context.resources) {
                            addFields(listOf(
                                CellFieldValue(
                                    id = R.id.ps_payment_purpose,
                                    title = getString(R.string.ps_payment_purpose),
                                    subtitle = MutableLiveData(it.data.purpose),
                                    isValueCell = true,
                                ),
                                CellFieldValue(
                                    id = R.id.ps_paid,
                                    title = getString(R.string.ps_paid),
                                    subtitle = MutableLiveData(makeSum(it.data.amountToPay + it.data.commission)),
                                    isValueCell = true,
                                ),
                            ))
                        }
                    is Response.Error -> println("Error")
                    else -> {}
                }
            }
        }
    }

    override fun viewCheckAction(arg: String) {
        action.value = FinePaymentDoneFragmentDirections.toViewCheckAction(arg)
    }
}