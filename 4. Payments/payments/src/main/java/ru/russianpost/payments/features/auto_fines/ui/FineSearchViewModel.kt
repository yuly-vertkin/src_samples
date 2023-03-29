package ru.russianpost.payments.features.auto_fines.ui

import androidx.lifecycle.MutableLiveData
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.NumberOrEmptyFieldValidator
import ru.russianpost.payments.base.domain.TemplateFieldFormatter
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel поиска штрафов по СТС и ВУ
 */
internal class FineSearchViewModel @Inject constructor(
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        val demoVrc = if (PaymentContract.isTestVersion) "9945666543" else ""
        val demoDl = /*if (PaymentContract.isTestVersion) "2233777999" else*/ ""
        val chargesData = repository.getData()

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_vehicle_registration_certificate,
                    title = getString(R.string.ps_vehicle_registration_certificate),
                    hint = getString(R.string.ps_vehicle_registration_certificate_hint),
                    text = MutableLiveData(demoVrc),
                    inputType = INPUT_TYPE_TEXT,
                    formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                    validator = NumberOrEmptyFieldValidator(VRC_LENGTH),
                    endDrawableRes = R.drawable.ic24_sign_question,
                    endDrawableColorRes = R.color.grayscale_stone,
                    onIconClickAction = ::showAdvice,
                    data = VRC_ADVICE,
                ),
                InputFieldValue(
                    id = R.id.ps_driver_license,
                    title = getString(R.string.ps_driver_license),
                    hint = getString(R.string.ps_driver_license_hint),
                    text = MutableLiveData(demoDl),
                    formatter = TemplateFieldFormatter(AUTO_DOCUMENTS_TEMPLATE),
                    validator = NumberOrEmptyFieldValidator(DL_LENGTH),
                    endDrawableRes = R.drawable.ic24_sign_question,
                    endDrawableColorRes = R.color.grayscale_stone,
                    onIconClickAction = ::showAdvice,
                    data = DL_ADVICE,
                ),
                CheckboxFieldValue(
                    id = R.id.ps_save_documents,
                    title = getString(R.string.ps_save_documents),
                    selected = MutableLiveData(chargesData.saveDocuments),
                ),
            ))

            addField(
                ButtonFieldValue(
                    text = MutableLiveData(getString(R.string.ps_search)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    action = ::onButtonClick,
                ),
                isMainFields = false
            )
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        // Если уже есть сохраненные документы, экран поиска не показывается
        val chargesData = repository.getData()
        if (!chargesData.vehicleRegistrationCertificates.isNullOrEmpty() ||
            !chargesData.driverLicenses.isNullOrEmpty()) {
            action.value = FineSearchFragmentDirections.toFinesAction()
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

        val buttonTitle = context.getString(R.string.ps_search)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        val vrc = getFieldText(R.id.ps_vehicle_registration_certificate).replace(" ", "")
        val dl = getFieldText(R.id.ps_driver_license).replace(" ", "")
        val chargesData = repository.getData().copy(
            vehicleRegistrationCertificates = if (vrc.isNotEmpty()) setOf(vrc) else null,
            driverLicenses = if (dl.isNotEmpty()) setOf(dl) else null,
            saveDocuments = getCheckboxValue(R.id.ps_save_documents),
        )

        if (chargesData.saveDocuments) {
            repository.saveData(chargesData)
        }

        action.value = FineSearchFragmentDirections.toFinesAction(chargesData)
    }
}