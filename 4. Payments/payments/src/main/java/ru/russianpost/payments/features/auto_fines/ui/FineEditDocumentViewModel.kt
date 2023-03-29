package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.domain.TemplateFieldFormatter
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFineEditDocumentParam
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams


/**
 * ViewModel редактирования документа для поиска штрафов
 */
internal class FineEditDocumentViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    private var param: AutoFineEditDocumentParam? = null

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FineEditDocumentViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FineEditDocumentViewModel
    }

    override fun onCreate() {
        super.onCreate()

        param = savedStateHandle.get<AutoFineEditDocumentParam>(FRAGMENT_PARAMS_NAME)
        val chargesData = repository.getData()

        with(context.resources) {
            title.value = when (param?.type) {
                AutoFineType.VRC -> getString(R.string.ps_vrc, param?.document)
                else -> getString(R.string.ps_dl, param?.document.orEmpty())
            }

            addField(
                when (param?.type) {
                    AutoFineType.VRC -> InputFieldValue(
                        id = R.id.ps_doc_number,
                        title = getString(R.string.ps_vrc_number),
                        text = MutableLiveData(param?.document),
                        hint = getString(R.string.ps_vehicle_registration_certificate_hint),
                        inputType = INPUT_TYPE_TEXT,
                        formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                        validator = BaseInputFieldValidator(VRC_LENGTH),
                        endDrawableRes = R.drawable.ic24_sign_question,
                        endDrawableColorRes = R.color.grayscale_stone,
                        onIconClickAction = ::showAdvice,
                        data = VRC_ADVICE,
                    )
                    else -> InputFieldValue(
                        id = R.id.ps_doc_number,
                        title = getString(R.string.ps_dl_number),
                        text = MutableLiveData(param?.document.orEmpty()),
                        hint = getString(R.string.ps_driver_license_hint),
                        formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                        validator = BaseInputFieldValidator(DL_LENGTH),
                        endDrawableRes = R.drawable.ic24_sign_question,
                        endDrawableColorRes = R.color.grayscale_stone,
                        onIconClickAction = ::showAdvice,
                        data = DL_ADVICE,
                    )
                }
            )

            addField(
                ButtonFieldValue(
                    text = MutableLiveData(getString(R.string.ps_save)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    action = ::onButtonClick,
                ),
                isMainFields = false
            )
        }
    }

    private fun showAdvice(data: Any?) {
        action.value = AutoFinesNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    /** menu delete */
    override fun onMenuItem1() {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_remove_doc_title),
                text = getString(R.string.ps_remove_doc_text),
                ok = getString(R.string.ps_ok_button),
                onOkClick = ::deleteDocument,
            )
        }
        dismissDialog.value = false
    }

    private fun deleteDocument() {
        val value = param?.document.orEmpty()

        var chargesData = repository.getData()
        chargesData = chargesData.copy(
            updateDocuments = true,
            vehicleRegistrationCertificates = removeValue(chargesData.vehicleRegistrationCertificates, value),
            driverLicenses = removeValue(chargesData.driverLicenses, value),
        )
        repository.saveData(chargesData)

        showSnackbar.value = SnackbarParams(R.string.ps_doc_removed)
        actionBack.value = true
    }

    private fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val oldValue = param?.document.orEmpty()
        val newValue = getFieldText(R.id.ps_doc_number).replace(" ", "")

        var chargesData = repository.getData()
        chargesData = chargesData.copy(
            updateDocuments = oldValue != newValue,
            vehicleRegistrationCertificates = replaceValue(chargesData.vehicleRegistrationCertificates, oldValue, newValue),
            driverLicenses = replaceValue(chargesData.driverLicenses, oldValue, newValue),
        )

        repository.saveData(chargesData)

        actionBack.value = true
    }

    private fun replaceValue(documents: Set<String>?, oldValue: String, newValue: String) : Set<String>? =
        documents?.map {
            if (it == oldValue) newValue else it
        }?.toSet()

    private fun removeValue(documents: Set<String>?, value: String) : Set<String>? =
        documents?.filter { it != value }?.toSet()
}