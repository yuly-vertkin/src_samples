package ru.russianpost.payments.base.ui

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import androidx.annotation.*
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.databinding.*

internal abstract class BaseFieldValue(
    open val id: Int,
    val type: ViewType,
    var containerId: Int? = null,
    var isVisible: Boolean = true,
    open var data: Any? = null,
) {
    abstract fun bind(parent: ViewGroup, binding: ViewDataBinding)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseFieldValue

        if (id != other.id) return false
        if (type != other.type) return false
        if (containerId != other.containerId) return false
        if (isVisible != other.isVisible) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + type.hashCode()
        result = 31 * result + (containerId ?: 0)
        result = 31 * result + isVisible.hashCode()
        result = 31 * result + (data?.hashCode() ?: 0)
        return result
    }
}

internal data class InputFieldValue(
    @IdRes override val id: Int,
    val title: String? = null,
    val text: MutableLiveData<String?> = MutableLiveData(null),
    var error: MutableLiveData<String> = MutableLiveData(""),
    val inputType: String = INPUT_TYPE_NUMBER,
    val imeOptions: String = IME_ACTION_NEXT,
    val hint: String? = null,
    val assistive: String? = null,
    @DrawableRes val endDrawableRes: Int = 0,
    @ColorRes val endDrawableColorRes: Int = 0,
    var enabled: Boolean = true,
    val formatter: BaseInputFieldFormatter = BaseInputFieldFormatter(),
    val validator: BaseInputFieldValidator = BaseInputFieldValidator(),
    val action: ((data: Any?) -> Unit)? = null,
    override var data: Any? = null,
    val onIconClickAction: ((data: Any?) -> Unit)? = null,
) : BaseFieldValue(id, ViewType.INPUT) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemInputViewBinding).fv = this
    }

    fun onIconClick() {
        onIconClickAction?.invoke(data)
    }
}

internal data class CellFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val title: String? = null,
    @ColorRes val titleColorRes: Int = 0,
    val subtitle: MutableLiveData<String?> = MutableLiveData(null),
    @DrawableRes val backgroundRes: Int = 0,
    @DrawableRes val startDrawableRes: Int = 0,
    @ColorRes val startDrawableColorRes: Int = 0,
    @DrawableRes val endDrawableRes: Int = 0,
    @ColorRes val endDrawableColorRes: Int = 0,
    @DimenRes val horizontalMarginRes: Int = 0,
    @DimenRes val verticalMarginRes: Int = 0,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
    val isValueCell: Boolean = false,
) : BaseFieldValue(id, if (!isValueCell) ViewType.CELL else ViewType.CELL_VALUE) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        if (!isValueCell) {
            (binding as PsItemCellViewBinding).fv = this
        } else {
            (binding as PsItemValueCellViewBinding).fv = this
        }
    }

    fun onClick() {
        action?.invoke(data)
    }
}

internal data class SpinnerFieldValue(
    @IdRes override val id: Int,
    val title: String? = null,
    val adapter: ArrayAdapter<String>,
    val items: List<String>,
    val enables: List<Boolean>? = null,
    val selected: MutableLiveData<String> = MutableLiveData(""),
) : BaseFieldValue(id, ViewType.SPINNER) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemSpinnerViewBinding).fv = this
    }
}

// TODO: понадобится для экранов госпошлин ЗАГС
//                AutoCompleteTextFieldValue(
//                    id = R.id.ps_status_payer,
//                    title = getString(R.string.ps_status_payer),
//                    inputType = INPUT_TYPE_TEXT,
//                    threshold = 3,
//                    items = listOf("1111", "1101", "1110", "2222", "2202", "2220", "3333", "3303", "3330"),
//                ),

internal data class AutoCompleteTextFieldValue(
    @IdRes override val id: Int,
    val title: String? = null,
    val text: MutableLiveData<String> = MutableLiveData(""),
    val inputType: String = INPUT_TYPE_NUMBER,
    val threshold: Int = AUTOCOMPLETE_DEFAULT_THRESOLD,
    val items: List<String>,
) : BaseFieldValue(id, ViewType.AUTO_COMPLETE_TEXT) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemAutoCompleteTextViewBinding).fv = this
    }
}

internal data class CheckboxFieldValue(
    @IdRes override val id: Int,
    val title: String? = null,
    val selected: MutableLiveData<Boolean> = MutableLiveData(false),
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
    val isRightCheckbox: Boolean = false,
) : BaseFieldValue(id, if (!isRightCheckbox) ViewType.CHECKBOX else ViewType.CHECKBOX_RIGHT) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        if (!isRightCheckbox) {
            (binding as PsItemCheckboxViewBinding).fv = this
        } else {
            (binding as PsItemCheckboxRightViewBinding).fv = this
        }
    }
}

internal data class TextFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val text: CharSequence?,
    val rightText: MutableLiveData<String?> = MutableLiveData(null),
    @ColorInt val textColor: Int,
    val textSize: Float,
    @DrawableRes val backgroundRes: Int = 0,
    @DimenRes val horizontalMarginRes: Int = 0,
    @DimenRes val verticalMarginRes: Int = 0,
    val gravity: Int = Gravity.LEFT,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
) : BaseFieldValue(id, ViewType.TEXT) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemTextViewBinding).fv = this
    }

    fun onClick() {
        action?.invoke(data)
    }
}

internal data class ImageFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val loadUrl: MutableLiveData<String> = MutableLiveData(""),
) : BaseFieldValue(id, ViewType.IMAGE) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemImageViewBinding).fv = this
    }
}

// we can use divider for making external space between fields:
// DividerFieldValue( heightRes = R.dimen.ps_zero_height, verticalMarginRes = R.dimen.text_vertical_margin,)

internal data class DividerFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    @DimenRes val heightRes: Int = R.dimen.ps_divider_height,
    @DimenRes val startMarginRes: Int = 0,
    @DimenRes val verticalMarginRes: Int = 0,
) : BaseFieldValue(id, ViewType.DIVIDER) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemDividerViewBinding).fv = this
    }
}

internal data class ButtonFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val text: MutableLiveData<String?> = MutableLiveData(null),
    val enabled: MutableLiveData<Boolean> = MutableLiveData(true),
    val checked: MutableLiveData<Boolean> = MutableLiveData(false),
    val isProgress: MutableLiveData<Boolean> = MutableLiveData(false),
    @DimenRes val horizontalMarginRes: Int = 0,
    @DimenRes val verticalMarginRes: Int = 0,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
    val endDrawableRes: MutableLiveData<Int> = MutableLiveData(0),
    val onIconClickAction: ((data: Any?) -> Unit)? = null,
    val isExtendedButton: Boolean = true,
) : BaseFieldValue(id, if (isExtendedButton) ViewType.EXTENDED_BUTTON else ViewType.BUTTON_TOGGLE) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        if (isExtendedButton)
            (binding as PsItemExtendedButtonViewBinding).fv = this
        else
            (binding as PsItemButtonTogleViewBinding).fv = this
    }

    fun onClick() {
        action?.invoke(data)
    }

    fun onIconClick() {
        onIconClickAction?.invoke(data)
    }
}

internal data class EmptyFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    var title: String? = null,
    val subtitle: MutableLiveData<String?> = MutableLiveData(null),
    @DrawableRes val drawableRes: Int = 0,
    @ColorRes val drawableColorRes: Int = 0,
    @DimenRes val horizontalMarginRes: Int = 0,
    @DimenRes val verticalMarginRes: Int = 0,
    var actionText: String? = null,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
) : BaseFieldValue(id, ViewType.EMPTY_VIEW) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemEmptyViewBinding).fv = this
    }

    fun onClick() {
        action?.invoke(data)
    }
}

internal data class ContainerFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val items: List<BaseFieldValue>,
) : BaseFieldValue(id, ViewType.CONTAINER) {

    override fun bind(parent: ViewGroup, _binding: ViewDataBinding) {
        val binding = _binding as PsItemContainerViewBinding
        binding.fv = this
        val view = binding.root as ViewGroup

        items.forEach { field ->
            val viewHolder = createViewHolderByType(parent, field.type.ordinal)
            viewHolder.bind(field)
            view.addView(viewHolder.itemView)
            val lp = viewHolder.itemView.layoutParams as LinearLayout.LayoutParams
            lp.weight = 1.0f
            view.layoutParams = lp
        }
    }
}

internal data class ChargeFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val violation: String?,
    val date: String,
    val sum: CharSequence?,
    val details: String,
    val isDetailVisible: Boolean = true,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
) : BaseFieldValue(id, ViewType.AUTO_FINE) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemChargeViewBinding).fv = this
    }

    fun onClick() {
        action?.invoke(data)
    }
}

internal data class HistoryFieldValue(
    @IdRes override val id: Int = View.generateViewId(),
    val title: String? = null,
    val date: String,
    val sum: CharSequence?,
    val details: String? = null,
    override var data: Any? = null,
    val action: ((data: Any?) -> Unit)? = null,
) : BaseFieldValue(id, ViewType.HISTORY) {

    override fun bind(parent: ViewGroup, binding: ViewDataBinding) {
        (binding as PsItemHistoryViewBinding).fv = this
    }

    fun onClick() {
        action?.invoke(data)
    }
}

