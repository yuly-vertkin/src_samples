package ru.russianpost.payments.features.payment_card.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.PaymentDoneParams
import javax.inject.Inject

/**
 * ViewModel нижнего диалог фрагмента
 */
internal class PaymentDoneDialogViewModel @Inject constructor(
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    title = getString(R.string.ps_save_check),
                    startDrawableRes = R.drawable.ic24_action_download,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSaveCheck,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_send_check),
                    startDrawableRes = R.drawable.ic24_action_share_v2,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSendCheck,
                ),
            ))
        }
    }

    private fun onSaveCheck(data: Any?) {
        action.value = PaymentDoneDialogFragmentDirections.toPaymentDoneAction(PaymentDoneParams.SAVE_CHECK)
    }

    private fun onSendCheck(data: Any?) {
        action.value = PaymentDoneDialogFragmentDirections.toPaymentDoneAction(PaymentDoneParams.SEND_CHECK)
    }
}