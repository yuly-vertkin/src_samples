package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.ListAdapter
import ru.russianpost.payments.R
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.tools.SnackbarParams
import java.text.DecimalFormat
import java.util.*

internal enum class DialogTypes {
    SERVICE_UNAVAILABLE, PAYMENT_ERROR,
}

/**
 * Базовая модель
 */
@SuppressLint("StaticFieldLeak")
internal abstract class BaseViewModel(appContextProvider: AppContextProvider) : ViewModel() {
    protected val context = appContextProvider.getContext()
    protected val fields = LinkedHashMap<Int, BaseFieldValue>()

    val rvAdapter = MutableLiveData<ListAdapter<*, *>>(ViewsAdapter())
    val items = MutableLiveData<List<BaseFieldValue>>()
    val action = MutableLiveData<NavDirections?>()
    val actionBack = MutableLiveData<Boolean>()
    val showSnackbar = MutableLiveData<SnackbarParams?>()
    val showDialog = MutableLiveData<DialogTypes?>()

    val title = MutableLiveData<String>()
    val btnLabel = MutableLiveData<String>()
    val isBtnVisible = MutableLiveData<Boolean>(true)
    val isBtnLoading = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>(false)

    open fun onCreateView() {
        fields.clear()
        action.value = null
        actionBack.value = false
        showSnackbar.value = null
        showDialog.value = null
        btnLabel.value = context.resources.getString(R.string.ps_proceed)
    }

    open fun onViewCreated() {
    }

    fun getFieldSize() = fields.size

//    operator fun <T : BaseFieldValue> get(id: Int) : T? =
//        fields[id] as? T

    fun <T : BaseFieldValue> get(id: Int) : T? =
        fields[id] as? T

    fun update() {
        items.value = fields.values.toList().filter { it.isVisible && it.containerId == null }
    }

    fun setVisibility(id: Int, isVisible: Boolean) {
        fields[id]?.let {
            if (it.isVisible != isVisible) {
                it.isVisible = isVisible
                update()
            }
        }
    }

    fun addField(value: BaseFieldValue) {
        val id = value.id
        fields[id] = value
        update()
    }

    fun addFields(values: List<BaseFieldValue>, containerId: Int? = null) {
        values.forEach { value ->
            val id = value.id
            fields[id] = value
            if (containerId != null)
                value.containerId = containerId
            if (value is ContainerFieldValue)
                addFields(value.items, value.id)
        }
        update()
    }

    fun getFieldText(id: Int) : String =
        fields[id]?.let { fieldValue ->
            when(fieldValue) {
                is InputFieldValue -> fieldValue.text.value
                is SpinnerFieldValue -> fieldValue.selected.value
                else -> ""
            }
        }.orEmpty()

    fun setFieldText(id: Int, text: String) {
        fields[id]?.let { fieldValue ->
            when(fieldValue) {
                is InputFieldValue -> {
                    fieldValue.text.value = text
                    fieldValue.error.value = ""
                }
                is CellFieldValue -> fieldValue.subtitle.value = text
            }
        }
    }

    fun setFieldError(id: Int, text: String) {
        (fields[id] as? InputFieldValue)?.error?.value = text
    }

    fun validateAll(resources: Resources) : Boolean {
        var result = true
        fields.values.toList().filterIsInstance<InputFieldValue>().forEach {
            it.error.value = it.validator.validate(resources, it.text.value, true).apply {
                if (this.isNotEmpty())  {
                    result = false
                }
            }
        }
        return result
    }

    fun getCheckboxValue(id: Int) : Boolean =
        fields[id]?.let { fieldValue ->
            when(fieldValue) {
                is CheckboxFieldValue -> fieldValue.selected.value
                else -> false
            }
        } ?: false

    open fun onButtonClick() {}

    protected fun makeSum(sum: Float?) =
        DecimalFormat("0.## $rubSign").format(sum)

    companion object {
        val rubSign = Currency.getInstance("RUB").getSymbol(Locale("ru", "RU"))
    }
}
