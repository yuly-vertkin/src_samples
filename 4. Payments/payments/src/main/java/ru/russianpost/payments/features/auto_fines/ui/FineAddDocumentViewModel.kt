package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.domain.TemplateFieldFormatter
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject


/**
 * ViewModel добавления документа для поиска штрафов
 */
@HiltViewModel
internal class FineAddDocumentViewModel @Inject constructor(
    private val repository: AutoFinesRepository,
    private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var param: AutoFineType? = null

    override fun onCreateView() {
        super.onCreateView()

        param = savedStateHandle.get<AutoFineType>(FRAGMENT_PARAMS_NAME)
        val autoFineData = repository.getData()

        with(context.resources) {
            title.value = when (param) {
                AutoFineType.VRC -> getString(R.string.ps_add_vrc)
                else -> getString(R.string.ps_add_dl)
            }

            autoFineData.let {
                addField(
                    when (param) {
                        AutoFineType.VRC -> InputFieldValue(
                            id = R.id.ps_doc_number,
                            title = getString(R.string.ps_vrc_number),
                            hint = getString(R.string.ps_vehicle_registration_certificate_hint),
                            formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                            validator = BaseInputFieldValidator(VRC_LENGTH),
                            endDrawableRes = R.drawable.ic24_sign_question,
                            onIconClickAction = ::showAdvice,
                            data = VRC_ADVICE,
                        )
                        else -> InputFieldValue(
                            id = R.id.ps_doc_number,
                            title = getString(R.string.ps_dl_number),
                            hint = getString(R.string.ps_driver_license_hint),
                            formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                            validator = BaseInputFieldValidator(DL_LENGTH),
                            endDrawableRes = R.drawable.ic24_sign_question,
                            onIconClickAction = ::showAdvice,
                            data = DL_ADVICE,
                        )
                    }
                )
                btnLabel.value = getString(R.string.ps_add)
            }
        }
    }

    private fun showAdvice(data: Any?) {
        action.value = AutoFinesNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    override fun onButtonClick() {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, Snackbar.Style.ERROR)
            return
        }

        val value = getFieldText(R.id.ps_doc_number).replace(" ", "")
        var autoFineData = repository.getData()

        autoFineData = when (param) {
            AutoFineType.VRC -> repository.getData().copy(
                vehicleRegistrationCertificates = addValue(autoFineData.vehicleRegistrationCertificates, value),
            )
            else -> repository.getData().copy(
                driverLicenses = addValue(autoFineData.driverLicenses, value),
            )
        }

        repository.saveData(autoFineData)

        actionBack.value = true
    }

    private fun addValue(docList: List<String>?, value: String) : List<String> {
        val newList = docList?.toMutableList() ?: mutableListOf()
        newList.add(value)
        return newList
    }
}