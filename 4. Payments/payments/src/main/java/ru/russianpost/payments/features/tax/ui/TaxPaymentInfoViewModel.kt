package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.*
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.CardDetail
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.BaseCardWorkViewModel
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import ru.russianpost.payments.tools.CustomSpinnerAdapter
import ru.russianpost.payments.tools.SnackbarParams
import ru.russianpost.payments.tools.toFloatOrDefault
import javax.inject.Inject

/**
 * ViewModel ввода информации о платеже
 */
internal class TaxPaymentInfoViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    cardRepository: PaymentCardRepository,
    paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseCardWorkViewModel(cardRepository, paramsRepository, appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        val taxDetails = repository.getData()

        with(context.resources) {
            addFields(listOf(
                SpinnerFieldValue(
                    id = R.id.ps_payment_basis,
                    title = getString(R.string.ps_payment_basis),
                    adapter = CustomSpinnerAdapter(context, R.layout.ps_spinner_dropdown_item),
                    selected = MutableLiveData(taxDetails.paymentBasis),
                    items = getStringArray(R.array.ps_payment_basises).toList(),
                ),
                InputFieldValue(
                    id = R.id.ps_tax_period,
                    title = getString(R.string.ps_tax_period),
                    text = MutableLiveData(taxDetails.taxPeriod.orEmpty().substring(TAX_PERIOD_PREFIX.length)),
                    hint = getString(R.string.ps_tax_period_hint),
                    assistive = getString(R.string.ps_tax_period_assistive),
                    formatter = BaseInputFieldFormatter(YEAR_LENGTH),
                    validator = YearFieldValidator(),
//                TaxPeriodFieldFormatter(),
//                TaxPeriodFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_charges_uid_document,
                    title = getString(R.string.ps_charges_uid_document),
                    text = MutableLiveData(taxDetails.paymentAccrualUniqueIdentifier),
                    hint = getString(R.string.ps_charges_uid_document_hint),
                    assistive = getString(R.string.ps_charges_uid_document_assistive),
                    formatter = BaseInputFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_payment_purpose,
                    title = getString(R.string.ps_payment_purpose),
                    text = MutableLiveData(taxDetails.paymentPurpose),
                    hint = getString(R.string.ps_payment_purpose_hint),
                    inputType = INPUT_TYPE_TEXT,
                    formatter = BaseInputFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_payment_sum,
                    title = getString(R.string.ps_payment_sum),
                    text = MutableLiveData(taxDetails.sum.toString()),
                    hint = getString(R.string.ps_payment_sum_hint),
                    inputType = INPUT_TYPE_NUMBER_DECIMAL,
                    formatter = DecimalNumberFormatter(SUM_FRACTION_NUMBER, SUM_LENGTH),
                    validator = NonZeroNumberValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_email,
                    title = getString(R.string.ps_email),
                    text = MutableLiveData(cardRepository.getData().email),
                    hint = getString(R.string.ps_email_hint),
                    inputType = INPUT_TYPE_TEXT,
                    imeOptions = IME_ACTION_DONE,
                    formatter = BaseInputFieldFormatter(),
                    validator = EmailValidator(),
                ),
            ))

//            addField(
//                ButtonFieldValue(
//                    text = MutableLiveData(getString(R.string.ps_proceed)),
//                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
//                    action = ::onButtonClick,
//                ),
//                isMainFields = false
//            )
        }
    }
    override fun showAdvice(data: Any?) { }

    override fun geSelectCardAction(param: Array<CardDetail>) : NavDirections =
        TaxPaymentInfoFragmentDirections.selectCardAction(param)

    override fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val email = getFieldText(R.id.ps_email)
        cardRepository.saveData(cardRepository.getData().copy(email = email))

        val taxDetails = repository.getData().copy(
            paymentBasis = getFieldText(R.id.ps_payment_basis),
            taxPeriod = TAX_PERIOD_PREFIX + getFieldText(R.id.ps_tax_period),
//            taxPeriod = getFieldText(R.id.tax_period),
            paymentAccrualUniqueIdentifier = getFieldText(R.id.ps_charges_uid_document),
            paymentPurpose = getFieldText(R.id.ps_payment_purpose),
            sum = getFieldText(R.id.ps_payment_sum).toFloatOrDefault(0f),
        )

        processNetworkCall(
            action = { repository.sendTaxDetails(taxDetails) },
            onSuccess = {
                showSnackbar.value = SnackbarParams(R.string.ps_data_sent)
                taxDetails.id = it
                repository.saveData(taxDetails)
                action.value = TaxPaymentInfoFragmentDirections.toAuthDialogAction()
            },
            onError = { showServiceUnavailableDialog() },
        )
    }
}