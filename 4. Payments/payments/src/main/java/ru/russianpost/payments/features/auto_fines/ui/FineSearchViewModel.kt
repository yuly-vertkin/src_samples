package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.NumberOrEmptyFieldValidator
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.domain.TemplateFieldFormatter
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel поиска штрафов по СТС и ВУ
 */
@HiltViewModel
internal class FineSearchViewModel @Inject constructor(
    private val repository: AutoFinesRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val autoFineData = repository.getData()

        with(context.resources) {
            autoFineData.let {
                addFields(listOf(
                    InputFieldValue(
                        id = R.id.ps_vehicle_registration_certificate,
                        title = getString(R.string.ps_vehicle_registration_certificate),
                        hint = getString(R.string.ps_vehicle_registration_certificate_hint),
                        formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                        validator = NumberOrEmptyFieldValidator(VRC_LENGTH),
                        endDrawableRes = R.drawable.ic24_sign_question,
                        onIconClickAction = ::showAdvice,
                        data = VRC_ADVICE,
                    ),
                    InputFieldValue(
                        id = R.id.ps_driver_license,
                        title = getString(R.string.ps_driver_license),
                        hint = getString(R.string.ps_driver_license_hint),
                        formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                        validator = NumberOrEmptyFieldValidator(DL_LENGTH),
                        endDrawableRes = R.drawable.ic24_sign_question,
                        onIconClickAction = ::showAdvice,
                        data = DL_ADVICE,
                    ),
                    CheckboxFieldValue(
                        id = R.id.ps_save_documents,
                        title = getString(R.string.ps_save_documents),
                        selected = MutableLiveData(it.saveDocuments),
                    ),
                ))
                btnLabel.value = getString(R.string.ps_search)
            }
        }
    }

    override fun onViewCreated() {
        val params = paramsRepository.getData()
        if(params.id.isNotEmpty()) {
            action.value = FineSearchFragmentDirections.toPaymentDoneAction()
        } else {
            val autoFineData = repository.getData()
            if (!autoFineData.vehicleRegistrationCertificates.isNullOrEmpty() ||
                !autoFineData.driverLicenses.isNullOrEmpty()) {
                action.value = FineSearchFragmentDirections.toFinesAction()
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

        val vrc = getFieldText(R.id.ps_vehicle_registration_certificate).replace(" ", "")
        val dl = getFieldText(R.id.ps_driver_license).replace(" ", "")
        val autoFineData = repository.getData().copy(
            vehicleRegistrationCertificates = if (vrc.isNotEmpty()) listOf(vrc) else null,
            driverLicenses = if (dl.isNotEmpty()) listOf(dl) else null,
            saveDocuments = getCheckboxValue(R.id.ps_save_documents),
        )

        if (autoFineData.saveDocuments) {
            repository.saveData(autoFineData)
        }

        action.value = FineSearchFragmentDirections.toFinesAction(autoFineData)
    }
}