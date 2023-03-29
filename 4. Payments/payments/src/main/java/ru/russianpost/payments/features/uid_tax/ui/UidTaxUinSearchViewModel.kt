package ru.russianpost.payments.features.uid_tax.ui

import androidx.lifecycle.MutableLiveData
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.UidTaxNavGraphDirections
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.NumbersValidator
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel поиска налога по УИН
 */
internal class UidTaxUinSearchViewModel @Inject constructor(
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        val demoTaxDoc = if (PaymentContract.isTestVersion) "18207743220122499291" else ""

        with(context.resources) {
            addFields(listOf(
                InputFieldValue(
                    id = R.id.ps_uid_tax_search,
                    title = getString(R.string.ps_uid_tax_uin_num),
                    text = MutableLiveData(demoTaxDoc),
                    hint = getString(R.string.ps_uid_tax_num_hint),
                    assistive = getString(R.string.ps_uid_tax_uin_num_assistive),
                    formatter = BaseInputFieldFormatter(UIN_LENGTH2),
                    validator = NumbersValidator(listOf(UIN_LENGTH1, UIN_LENGTH2)),
                    endDrawableRes = R.drawable.ic24_sign_question,
                    endDrawableColorRes = R.color.grayscale_stone,
                    onIconClickAction = ::showAdvice,
                    data = UIN_TAX_ADVICE,
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

        val taxDocNum = getFieldText(R.id.ps_uid_tax_search)
        val chargesData = repository.getData().copy(uin = taxDocNum)
        repository.saveData(chargesData)

        action.value = UidTaxUinSearchFragmentDirections.uidTaxDetailFragmentAction()
    }
}