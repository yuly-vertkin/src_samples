package ru.russianpost.payments.features.auto_fines.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import javax.inject.Inject

/**
 * ViewModel нижнего диалога добавления СТС или ВУ
 */
internal class FineAddDocumentDialogViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    title = getString(R.string.ps_add_vrc),
                    action = ::onAddVrc,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_add_dl),
                    action = ::onAddDl,
                ),
            ))
        }
    }

    private fun onAddVrc(data: Any?) {
        action.value = FineAddDocumentDialogFragmentDirections.toFineAddDocumentAction(AutoFineType.VRC)
    }

    private fun onAddDl(data: Any?) {
        action.value = FineAddDocumentDialogFragmentDirections.toFineAddDocumentAction(AutoFineType.DL)
    }
}