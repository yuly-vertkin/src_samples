package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.base.ui.FRAGMENT_PARAMS_NAME
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.tools.formatReverseDate
import ru.russianpost.payments.tools.formatServerDate

/**
 * ViewModel реквизитов штрафа
 */
internal class FineRequisitesViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FineRequisitesViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FineRequisitesViewModel
    }

    override fun onCreate() {
        super.onCreate()

        val charge = savedStateHandle.get<Charge>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            charge?.let {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = R.dimen.ps_zero_height,
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
                        subtitle = MutableLiveData(it.payeeBankName),
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
                        subtitle = MutableLiveData(makeDiscountMessage(it)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_discount_amount),
                        subtitle = MutableLiveData(makeSum(it.amountToPay)),
                        isValueCell = true,
                    ),
                ))
            }
        }
    }

    private fun makeDiscountMessage(fine: Charge) =
        fine.discount?.let { context.resources.getString(R.string.ps_discount_msg,
            makeSum(fine.discount), formatReverseDate(fine.discountExpiry)) } ?:
        fine.discountExpiry?.let {
            context.resources.getString(R.string.ps_discount_ended_in, formatReverseDate(fine.discountExpiry))
        } ?: context.resources.getString(R.string.ps_discount_ended)
}