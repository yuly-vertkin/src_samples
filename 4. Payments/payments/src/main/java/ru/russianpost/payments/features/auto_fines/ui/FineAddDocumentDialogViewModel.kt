package ru.russianpost.payments.features.auto_fines.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import javax.inject.Inject

/**
 * ViewModel нижнего диалог добавления СТС или ВУ
 */
@HiltViewModel
internal class FineAddDocumentDialogViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

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
        isBtnVisible.value = false
    }

    private fun onAddVrc(data: Any?) {
        action.value = FineAddDocumentDialogFragmentDirections.toFineAddDocumentAction(AutoFineType.VRC)
    }

    private fun onAddDl(data: Any?) {
        action.value = FineAddDocumentDialogFragmentDirections.toFineAddDocumentAction(AutoFineType.DL)
    }
}