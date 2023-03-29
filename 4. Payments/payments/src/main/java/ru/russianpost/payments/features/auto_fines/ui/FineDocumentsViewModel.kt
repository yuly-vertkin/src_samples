package ru.russianpost.payments.features.auto_fines.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineEditDocumentParam
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import javax.inject.Inject

/**
 * ViewModel документов для поиска штрафов
 */
internal class FineDocumentsViewModel @Inject constructor(
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()
        clearAllFields()

        val chargesData = repository.getData()

        with(context.resources) {
            chargesData.vehicleRegistrationCertificates?.map {
                addFields(listOf(
                    CellFieldValue(
                        title = getString(R.string.ps_vrc, it),
                        endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                        endDrawableColorRes = R.color.grayscale_stone,
                        data = AutoFineEditDocumentParam(AutoFineType.VRC, it),
                        action = ::onEditDocument,
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    ),
                ))
            }
            chargesData.driverLicenses?.map {
                addFields(listOf(
                    CellFieldValue(
                        title = getString(R.string.ps_dl, it),
                        endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                        endDrawableColorRes = R.color.grayscale_stone,
                        data = AutoFineEditDocumentParam(AutoFineType.DL, it),
                        action = ::onEditDocument,
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    ),
                ))
            }
            addField(
                CellFieldValue(
                    title = getString(R.string.ps_add_documents),
                    titleColorRes = R.color.common_xenon,
                    startDrawableRes = R.drawable.ic24_action_add,
                    startDrawableColorRes = R.color.common_xenon,
                    action = ::onAddDocument,
                ),
            )
        }
    }

    private fun onEditDocument(data: Any?) {
        data?.let {
            action.value = FineDocumentsFragmentDirections.toFineEditDocumentAction(it as AutoFineEditDocumentParam)
        }
    }

    private fun onAddDocument(data: Any?) {
        action.value = FineDocumentsFragmentDirections.toFineAddDocumentDialogAction()
    }
}