package ru.russianpost.payments.features.uid_tax.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.UidTaxNavGraphDirections
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.SnackbarParams


/**
 * ViewModel редактирования документа для поиска налогов
 */
internal class UidTaxEditDocumentViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    private var param: String? = null

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<UidTaxEditDocumentViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): UidTaxEditDocumentViewModel
    }

    override fun onCreate() {
        super.onCreate()

        param = savedStateHandle.get<String>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            title.value = getString(R.string.ps_inn, param)

            addField(
                InputFieldValue(
                    id = R.id.ps_doc_number,
                    title = getString(R.string.ps_inn_number),
                    text = MutableLiveData(param),
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
                        text = MutableLiveData(getString(R.string.ps_save)),
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
        val value = param.orEmpty()

        var chargesData = repository.getData()
        chargesData = chargesData.copy(
            updateDocuments = true,
            inn = removeValue(chargesData.inn, value),
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

        val oldValue = param.orEmpty()
        val newValue = getFieldText(R.id.ps_doc_number).replace(" ", "")

        var chargesData = repository.getData()
        chargesData = chargesData.copy(
            updateDocuments = oldValue != newValue,
            inn = replaceValue(chargesData.inn, oldValue, newValue),
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