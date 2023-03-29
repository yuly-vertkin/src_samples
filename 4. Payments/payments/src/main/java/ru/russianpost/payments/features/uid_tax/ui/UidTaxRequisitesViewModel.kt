package ru.russianpost.payments.features.uid_tax.ui

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
import ru.russianpost.payments.tools.formatServerDate

/**
 * ViewModel реквизитов штрафа
 */
internal class UidTaxRequisitesViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<UidTaxRequisitesViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): UidTaxRequisitesViewModel
    }

    override fun onCreate() {
        super.onCreate()

        val charge = savedStateHandle.get<Charge>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            charge?.let {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = R.dimen.ps_zero_height,
//                    verticalMarginRes = R.dimen.text_vertical_margin,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_payment_purpose),
                        subtitle = MutableLiveData(it.purpose),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_recipient_name),
                        subtitle = MutableLiveData(it.payeeName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_uid),
                        subtitle = MutableLiveData(it.supplierBillID),
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
                        title = getString(R.string.ps_recipient_bank),
                        subtitle = MutableLiveData(it.payeeBankName),
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
                        title = getString(R.string.ps_full_name),
                        subtitle = MutableLiveData(it.payerName),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_doc_date),
                        subtitle = MutableLiveData(formatServerDate(it.billDate)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_sum_payment),
                        subtitle = MutableLiveData(makeSum(it.amountToPay)),
                        isValueCell = true,
                    ),
                ))
            }
        }
    }
}