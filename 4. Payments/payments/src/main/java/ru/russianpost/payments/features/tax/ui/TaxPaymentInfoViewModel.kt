package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.*
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import ru.russianpost.payments.tools.CustomSpinnerAdapter
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.tools.SnackbarParams
import ru.russianpost.payments.tools.toFloatOrDefault
import javax.inject.Inject

/**
 * ViewModel ввода информации о платеже
 */
@HiltViewModel
internal class TaxPaymentInfoViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

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
                    text = MutableLiveData(taxDetails.taxPeriod.substring(TAX_PERIOD_PREFIX.length)),
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
                    formatter = DecimalNumberFormatter(SUM_FRACTION_NUMBER),
                    validator = NonZeroNumberValidator(),
                ),
            ))
        }
    }

    override fun onButtonClick() {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, Snackbar.Style.ERROR)
            return
        }

        val taxDetails = repository.getData().copy(
            paymentBasis = getFieldText(R.id.ps_payment_basis),
            taxPeriod = TAX_PERIOD_PREFIX + getFieldText(R.id.ps_tax_period),
//            taxPeriod = getFieldText(R.id.tax_period),
            paymentAccrualUniqueIdentifier = getFieldText(R.id.ps_charges_uid_document),
            paymentPurpose = getFieldText(R.id.ps_payment_purpose),
            sum = getFieldText(R.id.ps_payment_sum).toFloatOrDefault(0f),
        )

        viewModelScope.launch {
            repository.sendTaxDetails(taxDetails).collect {
                isBtnLoading.value = it is Response.Loading
                when(it) {
                    is Response.Success -> {
                        showSnackbar.value = SnackbarParams(R.string.ps_data_sent)
                        taxDetails.id = it.data
                        repository.saveData(taxDetails)
                        action.value = TaxPaymentInfoFragmentDirections.toTaxPaymentConfirmationAction()
                    }
                    is Response.Error -> showDialog.value = DialogTypes.PAYMENT_ERROR
                    else -> {}
                }
            }
        }
    }
}