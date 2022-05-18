package ru.russianpost.payments.features.payment_card.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.*
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel оплаты картой
 */
@HiltViewModel
internal class PaymentCardViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val paymentCard = repository.getData()
        val sum = savedStateHandle.get<Float>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_card_number,
                    text = MutableLiveData(paymentCard.cardNumber),
                    hint = getString(R.string.ps_card_number_hint),
                    formatter = TemplateFieldFormatter(CARD_NUMBER_TEMPLATE),
                    validator = LuhnCardNumberValidator(),
                ),
                ContainerFieldValue(
                    id = R.id.ps_container1,
                    items = listOf(
                        InputFieldValue(
                            id = R.id.ps_valid_thru,
                            text = MutableLiveData(paymentCard.validThru),
                            hint = getString(R.string.ps_valid_thru_hint),
                            formatter = TemplateFieldFormatter(CARD_VALID_THRU_TEMPLATE),
                            validator = CardValidThruValidator(),
                        ),
                        InputFieldValue(
                            id = R.id.ps_cvc,
                            text = MutableLiveData(paymentCard.cvc),
                            hint = getString(R.string.ps_cvc_hint),
                            inputType = "numberPassword",
                            formatter = BaseInputFieldFormatter(CARD_CVC_LENGTH),
                            validator = BaseInputFieldValidator(CARD_CVC_LENGTH),
                        ),
                    ),
                ),
            ))
            btnLabel.value = getString(R.string.ps_pay_sum, makeSum(sum))
        }
    }

    override fun onButtonClick() {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, Snackbar.Style.ERROR)
            return
        }

        val paymentCard = repository.getData().copy(
            cardNumber = getFieldText(R.id.ps_card_number),
            validThru = getFieldText(R.id.ps_valid_thru),
            cvc = getFieldText(R.id.ps_cvc),
        )
        repository.saveData(paymentCard)

        action.value = PaymentCardFragmentDirections.toPaymentDoneAction()
    }
}