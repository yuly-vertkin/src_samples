package ru.russianpost.payments.features.payment_card.ui

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import ru.russianpost.payments.MainNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.PaymentDoneParams
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.tools.SnackbarParams

/**
 * ViewModel результатов платежа
 */
internal abstract class PaymentDoneViewModel (
    protected val savedStateHandle: SavedStateHandle,
    protected val cardRepository: PaymentCardRepository,
    protected val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    val onSaveAction = MutableLiveData<Boolean>()
    val onShareAction = MutableLiveData<String?>()

    override fun onCreate() {
        super.onCreate()

        onSaveAction.value = false
        onShareAction.value = null

        with(context.resources) {
            addFields(listOf(
                DividerFieldValue(
                    heightRes = R.dimen.ps_zero_height,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_done,
                    title = getString(R.string.ps_payment_done),
                    subtitle = MutableLiveData(getString(R.string.ps_payment_done_add)),
                    backgroundRes = R.drawable.ps_common_fantome_background,
                    startDrawableRes = R.drawable.ic24_action_done,
                    startDrawableColorRes = R.color.common_jardin,
                    verticalMarginRes = R.dimen.ps_banner_vertical_margin,
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                ),
// TODO: не нужно для MVP
/*
                CellFieldValue(
                    title = getString(R.string.ps_view_check),
                    startDrawableRes = R.drawable.ic24_finance_invoice,
                    startDrawableColorRes = R.color.grayscale_stone,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onViewCheck
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_save_check),
                    startDrawableRes = R.drawable.ic24_action_download,
                    startDrawableColorRes = R.color.grayscale_stone,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onSaveCheck
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_send_check),
                    startDrawableRes = R.drawable.ic24_action_share_v2,
                    startDrawableColorRes = R.color.grayscale_stone,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onShareCheck
                ),
                DividerFieldValue(
                    heightRes = R.dimen.ps_big_divider_height,
                ),
*/
            ))

            addField(
                ButtonFieldValue(
                    text = MutableLiveData(getString(R.string.ps_back_main)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    action = ::onButtonClick,
                ),
                isMainFields = false
            )
        }
    }

    override fun onViewCreated() {
        super.onViewCreated()

        when(savedStateHandle.get<PaymentDoneParams>(FRAGMENT_PARAMS_NAME)) {
            PaymentDoneParams.SAVE_CHECK -> onSaveCheck()
            PaymentDoneParams.SEND_CHECK -> onShareCheck()
            else -> {}
        }
    }

    protected fun getAndClearIdParam() : String? {
        val params = paramsRepository.getData()
        paramsRepository.saveData(params.copy(id = null))
        return params.id
    }

    fun onViewCheck(data: Any?) {
        shareCheck {
            viewCheckAction(it)
        }
    }

    protected abstract fun viewCheckAction(arg: String)

    fun onSaveCheck(data: Any? = null, isPermissionGranted: Boolean = false) {
        if (!isPermissionGranted &&
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
        ) {
            onSaveAction.value = true
            return
        }

        processNetworkCall(
            action = { cardRepository.saveCheckExt(context, CHECK_PDF_NAME) },
            onSuccess = { showSnackbar.value = SnackbarParams(R.string.ps_check_saved) },
            onError = { showSnackbar.value = SnackbarParams(R.string.ps_error_check_saved) },
        )
    }

    private fun onShareCheck(data: Any? = null) {
        shareCheck {
            onShareAction.value = it
        }
    }

    private fun shareCheck(callback: (String) -> Unit) {
        processNetworkCall(
            action = { cardRepository.saveCheckInt(context, CHECK_PDF_NAME) },
            onSuccess = { callback(it) },
            onError = { showSnackbar.value = SnackbarParams(R.string.ps_error_check_saved) },
        )
    }

    private fun onButtonClick(data: Any?) {
        action.value = MainNavGraphDirections.toMainFragmentAction()
    }
}