package ru.russianpost.payments.features.tax.ui

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.base.ui.TextFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import javax.inject.Inject

/**
 * ViewModel подтверждения платежа
 */
@HiltViewModel
internal class TaxPaymentConfirmationViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        with(context.resources) {
            addFields(listOf(
                DividerFieldValue(
                    heightRes = R.dimen.ps_zero_height,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_confirmation,
                    title = getString(R.string.ps_payment_confirmation),
                    backgroundRes = R.drawable.ps_payment_confirmation_background,
                    startDrawableRes = R.drawable.ic24_sign_warning_circle,
                    startDrawableColorRes = R.color.common_sky,
                    verticalMarginRes = R.dimen.ps_horizontal_margin,
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                ),
                TextFieldValue(
                    text = getString(R.string.ps_tax_payment_info_title),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    textSize = getDimension(R.dimen.ps_text_size_20sp),
                ),
                CellFieldValue(
                    id = R.id.ps_payment_purpose,
                    title = getString(R.string.ps_payment_purpose),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_sum,
                    title = getString(R.string.ps_payment_sum),
                    isValueCell = true,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_divider_vertical_margin,
                ),
                TextFieldValue(
                    text = getString(R.string.ps_tax_payer_info_title),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    textSize = getDimension(R.dimen.ps_text_size_20sp),
                ),
                CellFieldValue(
                    id = R.id.ps_full_name,
                    title = getString(R.string.ps_full_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_birth_date,
                    title = getString(R.string.ps_birth_date),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_mobile_phone,
                    title = getString(R.string.ps_mobile_phone),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_address,
                    title = getString(R.string.ps_address),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_citizenship,
                    title = getString(R.string.ps_citizenship),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_sender_id,
                    title = getString(R.string.ps_sender_id),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_series_and_number,
                    title = getString(R.string.ps_series_and_number),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_issued_by,
                    title = getString(R.string.ps_issued_by),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_when_issued,
                    title = getString(R.string.ps_when_issued),
                    isValueCell = true,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_divider_vertical_margin,
                ),
                TextFieldValue(
                    text = getString(R.string.ps_tax_recipient_info_title),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    textSize = getDimension(R.dimen.ps_text_size_20sp),
                ),
                CellFieldValue(
                    id = R.id.ps_organization_name,
                    title = getString(R.string.ps_organization_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_tax_id_number_recipient,
                    title = getString(R.string.ps_tax_id_number_recipient),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_bank_name,
                    title = getString(R.string.ps_bank_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_account_number,
                    title = getString(R.string.ps_account_number),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_bank_id_code,
                    title = getString(R.string.ps_bank_id_code),
                    isValueCell = true,
                ),
            ))
            btnLabel.value = getString(R.string.ps_confirm)
        }

        viewModelScope.launch {
            // TODO: пока не работает
            repository.getTaxConfirmation(/*taxDetails.id*/"8CpT3-202202012019").collect {
                when(it) {
                    is Response.Success -> {
                        setFieldText(R.id.ps_payment_purpose, it.data.paymentPurpose)
                        setFieldText(R.id.ps_payment_sum, it.data.postalTransferAmount)
                        setFieldText(R.id.ps_full_name, it.data.fullName)
                        setFieldText(R.id.ps_birth_date, it.data.dateOfBirth)
                        setFieldText(R.id.ps_mobile_phone, it.data.mobilePhone)
                        setFieldText(R.id.ps_address, it.data.address)
                        setFieldText(R.id.ps_citizenship, it.data.citizenship)
                        setFieldText(R.id.ps_sender_id, it.data.senderID)
                        setFieldText(R.id.ps_series_and_number, it.data.seriesAndNumber)
                        setFieldText(R.id.ps_issued_by, it.data.issuedBy)
                        setFieldText(R.id.ps_when_issued, it.data.dateOfIssue)
                        setFieldText(R.id.ps_organization_name, it.data.organizationName)
                        setFieldText(R.id.ps_tax_id_number_recipient, it.data.recipientIndividualTaxNumber)
                        setFieldText(R.id.ps_bank_name, it.data.bankName)
                        setFieldText(R.id.ps_account_number, it.data.accountNumber)
                        setFieldText(R.id.ps_bank_id_code, it.data.recipientBankIdentificationCode)
                    }
                    is Response.Error -> {
                        println("Error")
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onButtonClick() {
        val taxDetails = repository.getData()
        action.value = TaxPaymentConfirmationFragmentDirections.toPaymentCardAction(taxDetails.sum)
    }
}