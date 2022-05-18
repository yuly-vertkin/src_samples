package ru.russianpost.payments.features.auto_fines.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFine
import ru.russianpost.payments.tools.formatReverseDate
import ru.russianpost.payments.tools.formatServerDate
import javax.inject.Inject

/**
 * ViewModel реквизитов штрафа
 */
@HiltViewModel
internal class FineRequisitesViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var fine: AutoFine? = null

    override fun onCreateView() {
        super.onCreateView()

        fine = savedStateHandle.get<AutoFine>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            fine?.let {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = R.dimen.ps_zero_height,
//                    verticalMarginRes = R.dimen.text_vertical_margin,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_fine_details),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_20sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_doc_number),
                        subtitle = MutableLiveData(it.supplierBillID),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_fine_place),
                        subtitle = MutableLiveData(it.offensePlace),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_fine_date),
                        subtitle = MutableLiveData(formatServerDate(it.offenseDate)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_violation),
                        subtitle = MutableLiveData(it.legalAct),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_payment_purpose),
                        subtitle = MutableLiveData(it.purpose),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_bank_name),
                        subtitle = MutableLiveData(it.bankName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_doc_date),
                        subtitle = MutableLiveData(formatServerDate(it.billDate)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_bank_id_code),
                        subtitle = MutableLiveData(it.payeeBankBik),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_account_number),
                        subtitle = MutableLiveData(it.payeeCorrespondentBankAccount),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_budget_classification_code),
                        subtitle = MutableLiveData(it.kbk),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_oktmo),
                        subtitle = MutableLiveData(it.oktmo),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_name),
                        subtitle = MutableLiveData(it.payeeName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_tax_id_number_recipient),
                        subtitle = MutableLiveData(it.payeeInn),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_reason_code),
                        subtitle = MutableLiveData(it.payeeKpp),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_full_name),
                        subtitle = MutableLiveData(it.payerName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_payment_uid),
                        subtitle = MutableLiveData(it.paymentUid),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_charges_uid_document),
                        subtitle = MutableLiveData(it.supplierBillID),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_accrual_amount),
                        subtitle = MutableLiveData(makeSum(it.totalAmount)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_discount_message),
                        subtitle = MutableLiveData(getString(R.string.ps_discount_msg,
                            "${it.discountFixed} $rubSign",
                            formatReverseDate(it.discountExpiry))),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_discounted_amount),
                        subtitle = MutableLiveData(makeSum(it.discount)),
                        isValueCell = true,
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_divider_vertical_margin,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_money_order_form),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_20sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_transfer_sum),
                        subtitle = MutableLiveData(makeSum(it.totalAmount)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_full_name),
                        subtitle = MutableLiveData(it.payerName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_address),
                        subtitle = MutableLiveData(it.address),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_zip_code),
                        subtitle = MutableLiveData(it.postCode),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_mobile_phone),
                        subtitle = MutableLiveData(it.mobilePhone),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_id),
                        subtitle = MutableLiveData(it.senderId),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_series_and_number),
                        subtitle = MutableLiveData(it.seriesAndNumber),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_when_issued),
                        subtitle = MutableLiveData(it.whenIssued),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_issued_by),
                        subtitle = MutableLiveData(it.issuedBy),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_citizenship),
                        subtitle = MutableLiveData(it.citizenship),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sender_birth_date),
                        subtitle = MutableLiveData(it.birthDate),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_organization_name),
                        subtitle = MutableLiveData(it.payeeName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_tax_id_number_recipient),
                        subtitle = MutableLiveData(it.payeeInn),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_bank_name),
                        subtitle = MutableLiveData(it.bankName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_payment_account),
                        subtitle = MutableLiveData(it.payeeCorrespondentBankAccount),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_bank_id_code),
                        subtitle = MutableLiveData(it.payeeBankBik),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_payment_purpose),
                        subtitle = MutableLiveData(it.purpose),
                        isValueCell = true,
                    ),
                ))
            }
            isBtnVisible.value = false
        }
    }
}