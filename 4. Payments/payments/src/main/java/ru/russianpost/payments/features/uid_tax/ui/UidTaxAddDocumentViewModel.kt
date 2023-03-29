package ru.russianpost.payments.features.uid_tax.ui

import androidx.lifecycle.MutableLiveData
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.UidTaxNavGraphDirections
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel добавления документа для поиска налогов
 */
internal class UidTaxAddDocumentViewModel @Inject constructor(
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            title.value = getString(R.string.ps_add_inn)

            addField(
                InputFieldValue(
                    id = R.id.ps_doc_number,
                    title = getString(R.string.ps_inn_number),
                    hint = getString(R.string.ps_uid_tax_num_hint),
                    assistive = getString(R.string.ps_uid_tax_inn_num_assistive),
                    formatter = BaseInputFieldFormatter(INN_LENGTH),
                    validator = BaseInputFieldValidator(INN_LENGTH),
                    endDrawableRes = R.drawable.ic24_sign_question,
                    endDrawableColorRes = R.color.grayscale_stone,
                    onIconClickAction = ::showAdvice,
                    data = INN_ADVICE,
                ))
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
        action.value = UidTaxNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    private fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val value = getFieldText(R.id.ps_doc_number).replace(" ", "")
        var chargesData = repository.getData()
        chargesData = chargesData.copy(
                updateDocuments = chargesData.inn?.contains(value) == false,
                inn = addValue(chargesData.inn, value),
            )
        repository.saveData(chargesData)

        actionBack.value = true
    }

    private fun addValue(documents: Set<String>?, value: String) : Set<String> {
        val newDocuments = documents?.toMutableSet() ?: mutableSetOf()
        newDocuments.add(value)
        return newDocuments
    }
}