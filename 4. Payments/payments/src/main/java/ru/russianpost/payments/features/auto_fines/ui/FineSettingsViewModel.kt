package ru.russianpost.payments.features.auto_fines.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import javax.inject.Inject

/**
 * ViewModel настроек штрафа
 */
@HiltViewModel
internal class FineSettingsViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        addField(
            CellFieldValue(
                title = context.resources.getString(R.string.ps_documents),
                startDrawableRes = R.drawable.ic24_user_passport,
                endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                action = ::onShowDocuments,
            ),
        )
        isBtnVisible.value = false
    }

    private fun onShowDocuments(data: Any?) {
        action.value = FineSettingsFragmentDirections.toFineDocumentsAction()
    }
}