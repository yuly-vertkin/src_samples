package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.NumberOrEmptyFieldValidator
import ru.russianpost.payments.base.domain.TemplateFieldFormatter
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineEditDocumentParam
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject


/**
 * ViewModel редактирования документа для поиска штрафов
 */
@HiltViewModel
internal class FineEditDocumentViewModel @Inject constructor(
    private val repository: AutoFinesRepository,
    private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var param: AutoFineEditDocumentParam? = null

    override fun onCreateView() {
        super.onCreateView()

        param = savedStateHandle.get<AutoFineEditDocumentParam>(FRAGMENT_PARAMS_NAME)
        val autoFineData = repository.getData()

        with(context.resources) {
            title.value = when (param?.type) {
                AutoFineType.VRC -> getString(R.string.ps_vrc, param?.document)
                else -> getString(R.string.ps_dl, param?.document.orEmpty())
            }

            autoFineData.let {
                addField(
                    when (param?.type) {
                        AutoFineType.VRC -> InputFieldValue(
                            id = R.id.ps_doc_number,
                            title = getString(R.string.ps_vrc_number),
                            text = MutableLiveData(param?.document),
                            hint = getString(R.string.ps_vehicle_registration_certificate_hint),
                            formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                            validator = NumberOrEmptyFieldValidator(VRC_LENGTH),
                            endDrawableRes = R.drawable.ic24_sign_question,
                            onIconClickAction = ::showAdvice,
                            data = VRC_ADVICE,
                        )
                        else -> InputFieldValue(
                            id = R.id.ps_doc_number,
                            title = getString(R.string.ps_dl_number),
                            text = MutableLiveData(param?.document.orEmpty()),
                            hint = getString(R.string.ps_driver_license_hint),
                            formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                            validator = NumberOrEmptyFieldValidator(DL_LENGTH),
                            endDrawableRes = R.drawable.ic24_sign_question,
                            onIconClickAction = ::showAdvice,
                            data = DL_ADVICE,
                        )
                    }
                )
                btnLabel.value = getString(R.string.ps_save)
            }
        }
    }

    private fun showAdvice(data: Any?) {
        action.value = FineSearchFragmentDirections.toAdviceAction((data as? String).orEmpty())
    }

    fun onActionDelete() {
        val value = param?.document.orEmpty()

        var autoFineData = repository.getData()
        autoFineData = repository.getData().copy(
            vehicleRegistrationCertificates = removeValue(autoFineData.vehicleRegistrationCertificates, value),
            driverLicenses = removeValue(autoFineData.driverLicenses, value),
        )
        repository.saveData(autoFineData)

        showSnackbar.value = SnackbarParams(R.string.ps_doc_removed)

        actionBack.value = true
    }

    override fun onButtonClick() {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, Snackbar.Style.ERROR)
            return
        }

        val oldValue = param?.document.orEmpty()
        val newValue = getFieldText(R.id.ps_doc_number).replace(" ", "")

        var autoFineData = repository.getData()
        autoFineData = repository.getData().copy(
            vehicleRegistrationCertificates = replaceValue(autoFineData.vehicleRegistrationCertificates, oldValue, newValue),
            driverLicenses = replaceValue(autoFineData.driverLicenses, oldValue, newValue),
        )

        repository.saveData(autoFineData)

        actionBack.value = true
    }

    private fun replaceValue(docList: List<String>?, oldValue: String, newValue: String) =
        docList?.map {
            if(it == oldValue) newValue else it
        }

    private fun removeValue(docList: List<String>?, value: String) =
        docList?.filter { it != value }
}