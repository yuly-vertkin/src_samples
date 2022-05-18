package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.domain.PrefixValidator
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.tax.TreasuryData
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel ввода информации о получателе
 */
@HiltViewModel
internal class TaxRecipientInfoViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val taxDetails = repository.getData()

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_bank_id_code,
                    title = getString(R.string.ps_bank_id_code),
                    text = MutableLiveData(taxDetails.recipientBankIdentificationCode),
                    hint = getString(R.string.ps_bank_id_code_hint),
                    formatter = BaseInputFieldFormatter(BANK_ID_CODE_LENGTH),
                    validator = BaseInputFieldValidator(BANK_ID_CODE_LENGTH),
                    action = ::getTreasuryData,
                ),
                InputFieldValue(
                    id = R.id.ps_account_number,
                    title = getString(R.string.ps_account_number),
                    text = MutableLiveData(taxDetails.accountNumber),
                    hint = getString(R.string.ps_account_number_hint),
                    assistive = getString(R.string.ps_account_number_assistive),
                    formatter = BaseInputFieldFormatter(ACCOUNT_NUMBER_LENGTH),
                    validator = PrefixValidator(ACCOUNT_NUMBER_PREFIX, ACCOUNT_NUMBER_LENGTH),
//                    action = ::handleBicVisibilty,
                ),
                InputFieldValue(
                    id = R.id.ps_budget_classification_code,
                    title = getString(R.string.ps_budget_classification_code),
                    text = MutableLiveData(taxDetails.budgetClassificationCode),
                    hint = getString(R.string.ps_budget_classification_code_hint),
                    formatter = BaseInputFieldFormatter(BUDGET_CLASSIFICATION_CODE_LENGTH),
                    validator = BaseInputFieldValidator(BUDGET_CLASSIFICATION_CODE_LENGTH),
                ),
                InputFieldValue(
                    id = R.id.ps_oktmo,
                    title = getString(R.string.ps_oktmo),
                    text = MutableLiveData(taxDetails.oktmo),
                    hint = getString(R.string.ps_oktmo_hint),
                    formatter = BaseInputFieldFormatter(OKTMO_LENGTH),
                    validator = BaseInputFieldValidator(OKTMO_LENGTH),
                ),
                InputFieldValue(
                    id = R.id.ps_tax_id_number_recipient,
                    title = getString(R.string.ps_tax_id_number_recipient),
                    text = MutableLiveData(taxDetails.recipientIndividualTaxNumber),
                    hint = getString(R.string.ps_tax_id_number_recipient_hint),
                    formatter = BaseInputFieldFormatter(TAX_ID_NUMBER_RECIPIENT_LENGTH),
                    validator = BaseInputFieldValidator(TAX_ID_NUMBER_RECIPIENT_LENGTH),
                ),
                InputFieldValue(
                    id = R.id.ps_reason_code,
                    title = getString(R.string.ps_reason_code),
                    text = MutableLiveData(taxDetails.recipientRegistrationReasonCode),
                    hint = getString(R.string.ps_reason_code_hint),
                    formatter = BaseInputFieldFormatter(REASON_CODE_LENGTH),
                    validator = BaseInputFieldValidator(REASON_CODE_LENGTH),
                ),
                InputFieldValue(
                    id = R.id.ps_bank_name,
                    title = getString(R.string.ps_bank_name),
                    text = MutableLiveData(taxDetails.bankName),
                    enabled = false,
                ),
                InputFieldValue(
                    id = R.id.ps_correspondent_account,
                    title = getString(R.string.ps_correspondent_account),
                    text = MutableLiveData(taxDetails.correspondentAccount),
                    enabled = false,
                ),
                InputFieldValue(
                    id = R.id.ps_recipient_name,
                    title = getString(R.string.ps_recipient_name),
                    text = MutableLiveData(taxDetails.recipientName),
                    enabled = false,
                ),
            ))
        }
    }

    override fun onViewCreated() {
        super.onViewCreated()

        val params = paramsRepository.getData()
        if(params.id.isNotEmpty())
            action.value = TaxRecipientInfoFragmentDirections.toPaymentDoneAction()
    }

    private fun handleBicVisibilty(data: Any?) {
//        val accNumField: InputFieldValue? = this[R.id.account_number]
//        val accNum = accNumField?.text?.value.orEmpty()
        val accNum = getFieldText(R.id.ps_account_number)
        setVisibility(R.id.ps_bank_id_code, !accNum.startsWith("02"))
    }

    private fun getTreasuryData(data: Any?) {
        val bic = getFieldText(R.id.ps_bank_id_code)
        if (bic.length == BANK_ID_CODE_LENGTH) {
            viewModelScope.launch {
                repository.getTreasuryData(bic).collect {
                    if (it is Response.Success) {
                        val result: TreasuryData = it.data
                        setFieldText(R.id.ps_bank_name, result.bankName)
                        setFieldText(R.id.ps_correspondent_account, result.correspondentAccount)
                        setFieldText(R.id.ps_recipient_name, result.recipientName)
                    } else if (it is Response.Error) {
                        setFieldError(R.id.ps_bank_id_code, context.resources.getString(R.string.ps_error_value))
                    }
                }
            }
        } else {
            setFieldText(R.id.ps_bank_name, "")
            setFieldText(R.id.ps_correspondent_account, "")
            setFieldText(R.id.ps_recipient_name, "")
        }
    }

    override fun onButtonClick() {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, Snackbar.Style.ERROR)
            return
        }

        val taxDetails = repository.getData().copy(
            recipientBankIdentificationCode = getFieldText(R.id.ps_bank_id_code),
            accountNumber = getFieldText(R.id.ps_account_number),
            budgetClassificationCode = getFieldText(R.id.ps_budget_classification_code),
            oktmo = getFieldText(R.id.ps_oktmo),
            bankName = getFieldText(R.id.ps_bank_name),
            correspondentAccount = getFieldText(R.id.ps_correspondent_account),
            recipientName = getFieldText(R.id.ps_recipient_name),
            recipientIndividualTaxNumber = getFieldText(R.id.ps_tax_id_number_recipient),
            recipientRegistrationReasonCode = getFieldText(R.id.ps_reason_code),
        )
        repository.saveData(taxDetails)

        action.value = TaxRecipientInfoFragmentDirections.toTaxPayerAction()
    }
}