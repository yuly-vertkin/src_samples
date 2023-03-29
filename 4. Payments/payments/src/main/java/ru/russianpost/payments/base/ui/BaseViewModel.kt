package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.text.Spanned
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.ListAdapter
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.tools.Log
import ru.russianpost.payments.tools.SnackbarParams
import java.text.DecimalFormat
import java.util.*

/**
 * Базовая модель
 */
@SuppressLint("StaticFieldLeak")
internal abstract class BaseViewModel(appContextProvider: AppContextProvider) : ViewModel() {
    protected val context = appContextProvider.getContext()
    private val mainFields = LinkedHashMap<Int, BaseFieldValue>()
    private val bottomFields = LinkedHashMap<Int, BaseFieldValue>()
    private val calledJobs = HashMap<String, Job>()

    val rvMainAdapter = MutableLiveData<ListAdapter<*, *>>(ViewsAdapter())
    val rvBottomAdapter = MutableLiveData<ListAdapter<*, *>>(ViewsAdapter())
    val mainItems = MutableLiveData<List<BaseFieldValue>>()
    val bottomItems = MutableLiveData<List<BaseFieldValue>>()
    val action = MutableLiveData<NavDirections?>()
    val actionBack = MutableLiveData<Boolean>()
    val actionFinish = MutableLiveData<Boolean>()
    val actionDeepLink = MutableLiveData<NavDeepLinkRequest?>()
    val showSnackbar = MutableLiveData<SnackbarParams?>()
    val showDialog = MutableLiveData<SimpleDialogParams?>()
    val dismissDialog = MutableLiveData<Boolean>()
    val fragmentResult = MutableLiveData<Any?>()

    val title = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>(false)

    open fun onCreate() {
        clearAllFields()
    }

    open fun onCreateView() {
        action.value = null
        actionBack.value = false
        actionFinish.value = false
        actionDeepLink.value = null
        showSnackbar.value = null
        showDialog.value = null
        fragmentResult.value = null
    }

    open fun onViewCreated() {
        sendAnalyticsEvent(title.value, METRICS_TARGET_SELF, METRICS_ACTION_OPEN)
    }

    open fun onDestroyView() {
// TODO: обсудить с аналитиками нужно ли им событие закрытия экрана
//        sendAnalyticsEvent(title.value, METRICS_TARGET_SELF, METRICS_ACTION_CLOSE)
    }

    open fun handleOnBackPressed() {
        sendAnalyticsEvent(title.value, METRICS_TARGET_BACK, METRICS_ACTION_TAP)
    }

    fun getFieldSize(isMainFields: Boolean = true) =
        if (isMainFields) mainFields.size else bottomFields.size

//    operator fun <T : BaseFieldValue> get(id: Int) : T? =
//        fields[id] as? T

    fun <T : BaseFieldValue> get(id: Int, isMainFields: Boolean = true) : T? {
        val fields = if (isMainFields) mainFields else bottomFields
        return fields[id] as? T
    }

    fun clearFields(isMainFields: Boolean = true) {
        val fields = if (isMainFields) mainFields else bottomFields
        fields.clear()
        update(isMainFields)
    }

    protected fun clearAllFields() {
        mainFields.clear()
        bottomFields.clear()
    }

    private fun update(isMainFields: Boolean = true) {
        val fields = if (isMainFields) mainFields else bottomFields
        val items = if (isMainFields) mainItems else bottomItems
        items.value = fields.values.toList().filter { it.isVisible && it.containerId == null }
    }

    fun setVisibility(id: Int, isVisible: Boolean, isMainFields: Boolean = true) {
        val fields = if (isMainFields) mainFields else bottomFields
        fields[id]?.let {
            if (it.isVisible != isVisible) {
                it.isVisible = isVisible
                update(isMainFields)
            }
        }
    }

    fun addField(value: BaseFieldValue, isMainFields: Boolean = true) {
        val id = value.id
        val fields = if (isMainFields) mainFields else bottomFields
        fields[id] = value
        update(isMainFields)
    }

    fun addFields(values: List<BaseFieldValue>, containerId: Int? = null, isMainFields: Boolean = true) {
        val fields = if (isMainFields) mainFields else bottomFields
        values.forEach { value ->
            val id = value.id
            fields[id] = value
            if (containerId != null)
                value.containerId = containerId
            if (value is ContainerFieldValue)
                addFields(value.items, value.id, isMainFields)
        }
        update(isMainFields)
    }

    fun getFieldText(id: Int, isMainFields: Boolean = true) : String {
        val fields = if (isMainFields) mainFields else bottomFields
        return fields[id]?.let { fieldValue ->
            when (fieldValue) {
                is InputFieldValue -> fieldValue.text.value
                is CellFieldValue -> fieldValue.subtitle.value
                is SpinnerFieldValue -> fieldValue.selected.value
                is AutoCompleteTextFieldValue -> fieldValue.text.value
                is ButtonFieldValue -> fieldValue.text.value
                else -> ""
            }
        }.orEmpty()
    }

    fun setFieldText(id: Int, text: String?, isMainFields: Boolean = true) {
        val fields = if (isMainFields) mainFields else bottomFields
        fields[id]?.let { fieldValue ->
            when(fieldValue) {
                is InputFieldValue -> {
                    fieldValue.text.value = text
                    fieldValue.error.value = ""
                }
                is CellFieldValue -> fieldValue.subtitle.value = text
                is ButtonFieldValue -> fieldValue.text.value = text
                is TextFieldValue -> fieldValue.rightText.value = text
            }
        }
    }

    fun setFieldError(id: Int, text: String) {
        (mainFields[id] as? InputFieldValue)?.error?.value = text
    }

    fun validateAll(resources: Resources) : Boolean {
        var result = true
        mainFields.values.toList().filterIsInstance<InputFieldValue>().forEach {
            it.error.value = it.validator.validate(resources, it.text.value, true).apply {
                if (this.isNotEmpty())  {
                    Log.d("Field ${it.title} validate error: $this")
                    result = false
                }
            }
        }
        return result
    }

    fun getCheckboxValue(id: Int) : Boolean =
        mainFields[id]?.let { fieldValue ->
            when(fieldValue) {
                is CheckboxFieldValue -> fieldValue.selected.value
                else -> false
            }
        } ?: false

    protected fun makeSum(sum: Float?) =
        sum?.let {
            DecimalFormat("0.00 $rubSign").format(sum)
        }

    protected fun <T> processNetworkCall(
        action: () -> Flow<Result<T>>,
        onSuccess: ((T) -> Unit)? = null,
        onError: ((Throwable) -> Unit)? = null,
        callName: String = DEFAULT_CALL_NAME
    ) {
        // Мы не повторяем запрос, если предыдущий еще не отработал
        // Внимание! для корректной работы параллельных запросов необходимо дать им разные имена
        val isActive = calledJobs[callName]?.isActive ?: false
        if (!isActive) {
            calledJobs[callName] = viewModelScope.launch {
                action().collect {
                    isLoading.value = it is Result.Loading || isAnotherActiveNetworkCall(callName)
//                    isLoading.value = if (it is Result.Loading) isLoading.value!! + 1
//                    else if (isLoading.value!! > 0) isLoading.value!! - 1 else 0

                    when (it) {
                        is Result.Success -> onSuccess?.invoke(it.data)
                        is Result.Error -> {
                            if (it.error is ResponseException && it.error.errorCode == ERROR_NO_NETWORK)
                                showNoNetworkDialog()
                            else
                                onError?.invoke(it.error)
                        }
                        else -> { /* nothing to do */ }
                    }
                }
            }
        }
    }

    protected fun isAnotherActiveNetworkCall(callName: String = DEFAULT_CALL_NAME) =
        calledJobs.any {
            it.key != callName && it.value.isActive
        }

    open fun onFragmentResult(result: Bundle) {}

    /**
     * Показывает диалог в случаях ошибок считывания QR-кода
     * @param onCancelClick - callback вызываемый в случае нажатия на кнопку Cancel
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showScanErrorDialog(onCancelClick: (() -> Unit)? = null, onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_scan_title),
                text = getString(R.string.ps_error_scan_text),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
                onCancelClick = onCancelClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог когда нет сети
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showNoNetworkDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_no_network),
                text = getString(R.string.ps_error_try_later),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог в ситуациях, которых не должно быть (пустой УИН, не приходит описание ошибки с сервера)
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showServiceUnavailableDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_service_unavailable),
                text = getString(R.string.ps_error_try_later),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог когда отжат checkbox: Подтвердите, что не являетесь публичным должностным лицом
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showPublicOfficialDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_public_official_title),
                text = getString(R.string.ps_error_public_official_text),
                ok = getString(R.string.ps_well),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог при ошибке авторизации Почта ИД
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showAuthorizationErrorDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_authorization_title),
                text = getString(R.string.ps_error_authorization_text),
                ok = getString(R.string.ps_understand),
                isCancelable = false,
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог при ошибке авторизации в Госуслугах (ЕСИА)
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showAuthorizationEsiaErrorDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_authorization_esia_title),
                text = getString(R.string.ps_error_authorization_esia_text),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог при ошибке, возникающей сразу на запросе оплаты paymentCard (cps/test)
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showPaymentServiceErrorDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_payment_title),
                text = getString(R.string.ps_error_payment_service_text),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог при ошибке процессинга или отмене платежа
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showPaymentCardErrorDialog(onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = getString(R.string.ps_error_payment_card_title),
                text = getString(R.string.ps_error_payment_card_text),
                ok = getString(R.string.ps_understand),
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    /**
     * Показывает диалог при ошибках от сервера
     * @param errorCode - код ошибки
     * @param errorTitle - заголовок ошибки
     * @param errorMessage - текст ошибки
     * @param onOkClick - callback вызываемый в случае нажатия на кнопку Ok
     * */
    protected fun showServerErrorDialog(errorCode: Int, errorTitle: String? = null, errorMessage: String? = null, onOkClick: (() -> Unit)? = null) {
        with(context.resources) {
            showDialog.value = SimpleDialogParams(
                title = errorTitle ?: getString(R.string.ps_error_title),
                text = "${errorMessage ?: getString(R.string.ps_error_title)} ($errorCode)",
                ok = getString(R.string.ps_understand),
                isCancelable = false,
                onOkClick = onOkClick,
            )
        }
        dismissDialog.value = false
    }

    protected fun makeOfferText(@StringRes textRes: Int, @StringRes offerRes: Int) : Spanned {
        val offerColor = ContextCompat.getColor(context, R.color.common_xenon)
        val text = context.getString(textRes)
        val offer = context.getString(offerRes)
        return buildSpannedString {
            append(text)
            append(" ")
            color(offerColor) {
                append(offer)
            }
        }
    }

    companion object {
        val rubSign: String = Currency.getInstance("RUB").getSymbol(Locale("ru", "RU"))
        const val DEFAULT_CALL_NAME = "Call"
    }
}
