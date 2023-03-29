package ru.russianpost.payments.features.payment_card.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel ввода адреса плательщика
 */
internal class AddressDialogViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addFields(listOf(
                TextFieldValue(
                    text = getString(R.string.ps_reg_address_title),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    textSize = getDimension(R.dimen.ps_text_size_16sp),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_dimen_16dp,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_reg_address_text),
                    isValueCell = true
                ),
                InputFieldValue(
                    id = R.id.ps_sender_address,
                    title = getString(R.string.ps_reg_address_title),
                    hint = getString(R.string.ps_reg_address_hint),
                    inputType = INPUT_TYPE_TEXT,
                    imeOptions = IME_ACTION_DONE,
                ),
            ))

            addField(
                ContainerFieldValue(
                    id = R.id.ps_container1,
                    items = listOf(
                        ButtonFieldValue(
                            text = MutableLiveData(getString(R.string.ps_cancel_button)),
                            horizontalMarginRes = R.dimen.ps_horizontal_margin,
                            action = ::onCancelButtonClick,
                        ),
                        ButtonFieldValue(
                            text = MutableLiveData(getString(R.string.ps_ok_button)),
                            horizontalMarginRes = R.dimen.ps_horizontal_margin,
                            action = ::onOkButtonClick,
                        ),
                    ),
                ),
                isMainFields = false
            )
        }
    }

    private fun onOkButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val address = getFieldText(R.id.ps_sender_address)

        fragmentResult.value = address
        actionBack.value = true
    }

    private fun onCancelButtonClick(data: Any?) {
        actionBack.value = true
    }
}