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
import ru.russianpost.payments.entities.auto_fines.AutoFineType
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams

/**
 * ViewModel добавления документа для поиска штрафов
 */
internal class FineAddDocumentViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var param: AutoFineType? = null

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FineAddDocumentViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FineAddDocumentViewModel
    }

    override fun onCreate() {
        super.onCreate()

        param = savedStateHandle.get<AutoFineType>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            title.value = when (param) {
                AutoFineType.VRC -> getString(R.string.ps_add_vrc)
                else -> getString(R.string.ps_add_dl)
            }

            addField(
                when (param) {
                    AutoFineType.VRC -> InputFieldValue(
                        id = R.id.ps_doc_number,
                        title = getString(R.string.ps_vrc_number),
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
                    text = MutableLiveData(getString(R.string.ps_add)),
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

    private fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val value = getFieldText(R.id.ps_doc_number).replace(" ", "")
        var chargesData = repository.getData()
        chargesData = when (param) {
            AutoFineType.VRC -> repository.getData().copy(
                updateDocuments = chargesData.vehicleRegistrationCertificates?.contains(value) == false,
                vehicleRegistrationCertificates = addValue(chargesData.vehicleRegistrationCertificates, value),
            )
            else -> repository.getData().copy(
                updateDocuments = chargesData.driverLicenses?.contains(value) == false,
                driverLicenses = addValue(chargesData.driverLicenses, value),
            )
        }
        repository.saveData(chargesData)

        actionBack.value = true
    }

    private fun addValue(documents: Set<String>?, value: String) : Set<String> {
        val newDocuments = documents?.toMutableSet() ?: mutableSetOf()
        newDocuments.add(value)
        return newDocuments
    }
}