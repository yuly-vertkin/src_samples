package ru.russianpost.payments.features.uid_tax.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import javax.inject.Inject

/**
 * ViewModel настроек налогов
 */
internal class UidTaxSettingsViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        addField(
            CellFieldValue(
                title = context.resources.getString(R.string.ps_documents),
                startDrawableRes = R.drawable.ic24_user_passport,
                startDrawableColorRes = R.color.grayscale_stone,
                endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                endDrawableColorRes = R.color.grayscale_stone,
                action = ::onShowDocuments,
            ),
        )
    }

    private fun onShowDocuments(data: Any?) {
        action.value = UidTaxSettingsFragmentDirections.uidTaxDocumentsFragmentAction()
    }
}