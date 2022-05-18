package ru.russianpost.payments.features.payment_card.ui

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.russianpost.payments.MainNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.tools.SnackbarParams

enum class PaymentDoneParams {
    NONE, SAVE_CHECK, SEND_CHECK
}

/**
 * ViewModel результатов платежа
 */
internal abstract class PaymentDoneViewModel (
    private val savedStateHandle: SavedStateHandle,
    private val cardRepository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    val onSaveAction = MutableLiveData<Boolean>()
    val onShareAction = MutableLiveData<String?>()

    override fun onCreateView() {
        super.onCreateView()

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
                    backgroundRes = R.drawable.ps_payment_done_background,
                    startDrawableRes = R.drawable.ic24_action_done,
                    startDrawableColorRes = R.color.common_jardin,
                    verticalMarginRes = R.dimen.ps_banner_vertical_margin,
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_view_check,
                    title = getString(R.string.ps_view_check),
                    startDrawableRes = R.drawable.ic24_finance_invoice,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onViewCheck
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_save_check,
                    title = getString(R.string.ps_save_check),
                    startDrawableRes = R.drawable.ic24_action_download,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onSaveCheck
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_send_check,
                    title = getString(R.string.ps_send_check),
                    startDrawableRes = R.drawable.ic24_action_share_v2,
                    verticalMarginRes = R.dimen.ps_cell_vertical_margin,
                    action = ::onShareCheck
                ),
                DividerFieldValue(
                    heightRes = R.dimen.ps_big_divider_height,
                ),
            ))
            btnLabel.value = getString(R.string.ps_back_main)
        }
    }

    override fun onViewCreated() {
        when(savedStateHandle.get<PaymentDoneParams>(FRAGMENT_PARAMS_NAME)) {
            PaymentDoneParams.SAVE_CHECK -> onSaveCheck()
            PaymentDoneParams.SEND_CHECK -> onShareCheck()
        }
    }

    fun onViewCheck(data: Any?) {
        shareCheck() {
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

        viewModelScope.launch {
            cardRepository.saveCheckExt(context, CHECK_PDF_NAME).collect {
                when(it) {
                    is Response.Success -> showSnackbar.value = SnackbarParams(R.string.ps_check_saved)
                    is Response.Error -> showSnackbar.value = SnackbarParams(R.string.ps_error_check_saved)
                    else -> {}
                }
            }
        }
    }

    fun onShareCheck(data: Any? = null) {
        shareCheck() {
            onShareAction.value = it
        }
    }

    private fun shareCheck(callback: (String) -> Unit) {
        viewModelScope.launch {
            cardRepository.saveCheckInt(context, CHECK_PDF_NAME).collect {
                when(it) {
                    is Response.Success -> callback(it.data)
                    is Response.Error -> showSnackbar.value = SnackbarParams(R.string.ps_error_check_saved)
                    else -> {}
                }
            }
        }
    }

    override fun onButtonClick() {
        action.value = MainNavGraphDirections.toMainFragmentAction()
    }
}