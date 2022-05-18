package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.*
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import ru.russianpost.payments.tools.CustomSpinnerAdapter
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel ввода информации о плательщике
 */
@HiltViewModel
internal class TaxPayerInfoViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val taxDetails = repository.getData()

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_tax_id_number_payer,
                    title = getString(R.string.ps_tax_id_number_payer),
                    text = MutableLiveData(taxDetails.payerIndividualTaxNumber),
                    hint = getString(R.string.ps_tax_id_number_payer_hint),
                    formatter = BaseInputFieldFormatter(TAX_ID_NUMBER_PAYER_LENGTH),
                    validator = BaseInputFieldValidator(TAX_ID_NUMBER_PAYER_LENGTH),
                ),
                SpinnerFieldValue(
                    id = R.id.ps_status_payer,
                    title = getString(R.string.ps_status_payer),
                    adapter = CustomSpinnerAdapter(context, R.layout.ps_spinner_dropdown_item),
                    selected = MutableLiveData(taxDetails.payerStatus),
                    items = getStringArray(R.array.ps_status_payers).toList(),
                    enables = listOf(true, false),
                ),
                InputFieldValue(
                    id = R.id.ps_first_name,
                    title = getString(R.string.ps_first_name),
                    text = MutableLiveData(taxDetails.firstName),
                    hint = getString(R.string.ps_first_name_hint),
                    inputType = INPUT_TYPE_TEXT,
                    formatter = TextOnlyFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_patronymic,
                    title = getString(R.string.ps_patronymic),
                    text = MutableLiveData(taxDetails.patronymic),
                    hint = getString(R.string.ps_patronymic_hint),
                    assistive = getString(R.string.ps_patronymic_assistive),
                    inputType = INPUT_TYPE_TEXT,
                    formatter = TextOnlyFieldFormatter(),
                    validator = EmptyFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_last_name,
                    title = getString(R.string.ps_last_name),
                    text = MutableLiveData(taxDetails.lastName),
                    hint = getString(R.string.ps_last_name_hint),
                    inputType = INPUT_TYPE_TEXT,
                    formatter = TextOnlyFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.ps_address,
                    title = getString(R.string.ps_address),
                    text = MutableLiveData(taxDetails.payerAddress),
                    hint = getString(R.string.ps_address_hint),
                    formatter = BaseInputFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
// not used now
/*
                InputFieldValue(
                    id = R.id.email,
                    title = getString(R.string.email),
                    text = MutableLiveData(taxDetails.email),
                    hint = getString(R.string.email_hint),
                    formatter = BaseInputFieldFormatter(),
                    validator = EmailValidator(),
                ),
                InputFieldValue(
                    id = R.id.service_payer_id,
                    title = getString(R.string.service_payer_id),
                    text = MutableLiveData(taxDetails.servicePayerIdentifier),
                    formatter = BaseInputFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
                InputFieldValue(
                    id = R.id.payment_uid,
                    title = getString(R.string.payment_uid),
                    text = MutableLiveData(taxDetails.uniquePaymentIdentifier),
                    formatter = BaseInputFieldFormatter(),
                    validator = BaseInputFieldValidator(),
                ),
*/
                CheckboxFieldValue(
                    id = R.id.ps_confirm_not_public_official,
                    title = getString(R.string.ps_confirm_not_public_official),
                    selected = MutableLiveData(taxDetails.notPublicOfficial),
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
            payerIndividualTaxNumber = getFieldText(R.id.ps_tax_id_number_payer),
            payerStatus = getFieldText(R.id.ps_status_payer),
            firstName = getFieldText(R.id.ps_first_name),
            patronymic = getFieldText(R.id.ps_patronymic),
            lastName = getFieldText(R.id.ps_last_name),
            payerAddress = getFieldText(R.id.ps_address),
//            email = getFieldText(R.id.email),
//            servicePayerIdentifier = getFieldText(R.id.service_payer_id),
//            uniquePaymentIdentifier = getFieldText(R.id.payment_uid),
            notPublicOfficial = getCheckboxValue(R.id.ps_confirm_not_public_official),
        )
        repository.saveData(taxDetails)

        action.value = TaxPayerInfoFragmentDirections.toTaxPaymentAction()
    }
}