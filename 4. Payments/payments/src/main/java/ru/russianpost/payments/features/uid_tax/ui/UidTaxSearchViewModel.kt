package ru.russianpost.payments.features.uid_tax.ui

import androidx.lifecycle.MutableLiveData
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.UidTaxNavGraphDirections
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel поиска налога по ИНН
 */
internal class UidTaxSearchViewModel @Inject constructor(
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        val demoTaxDoc = if (PaymentContract.isTestVersion) "032618162617" else ""
        val chargesData = repository.getData()

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_uid_tax_search,
                    title = getString(R.string.ps_uid_tax_inn_num),
                    text = MutableLiveData(demoTaxDoc),
                    hint = getString(R.string.ps_uid_tax_num_hint),
                    assistive = getString(R.string.ps_uid_tax_inn_num_assistive),
                    formatter = BaseInputFieldFormatter(INN_LENGTH),
                    validator = BaseInputFieldValidator(INN_LENGTH),
                    endDrawableRes = R.drawable.ic24_sign_question,
                    endDrawableColorRes = R.color.grayscale_stone,
                    onIconClickAction = ::showAdvice,
                    data = INN_ADVICE,
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
        if (!chargesData.inn.isNullOrEmpty()) {
            action.value = UidTaxSearchFragmentDirections.uidTaxesFragmentAction()
        }
    }

    private fun showAdvice(data: Any?) {
        action.value = UidTaxNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    private fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val buttonTitle = context.getString(R.string.ps_search)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        val inn = getFieldText(R.id.ps_uid_tax_search).replace(" ", "")
        val chargesData = repository.getData().copy(
            inn = if (inn.isNotEmpty()) setOf(inn) else null,
            saveDocuments = getCheckboxValue(R.id.ps_save_documents),
        )

        if (chargesData.saveDocuments) {
            repository.saveData(chargesData)
        }

        action.value = UidTaxSearchFragmentDirections.uidTaxesFragmentAction(chargesData)
    }
}