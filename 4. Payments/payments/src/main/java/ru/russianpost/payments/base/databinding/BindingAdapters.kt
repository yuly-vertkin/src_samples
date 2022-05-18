package ru.russianpost.payments.base.databinding

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.russianpost.mobileapp.widget.CellView
import ru.russianpost.mobileapp.widget.CompoundButtonView
import ru.russianpost.mobileapp.widget.ExtendedButtonView
import ru.russianpost.mobileapp.widget.InputView
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.tools.CustomSpinnerAdapter

@BindingAdapter("adapter")
internal fun setRecyclerViewAdapter(recyclerView: RecyclerView, adapter: ListAdapter<*, *>) {
    recyclerView.adapter = adapter
}

@BindingAdapter("listData")
internal fun bindRecyclerView(recyclerView: RecyclerView, data: List<BaseFieldValue>?) {
    (recyclerView.adapter as ListAdapter<BaseFieldValue, *>).submitList(data)
}

@BindingAdapter("isVisible")
internal fun viewVisibility(view: View, isVisible: Boolean) {
    view.visibility = if(isVisible) View.VISIBLE else View.GONE
}

@BindingAdapter("enabled")
internal fun setEnabled(view: InputView, enabled: Boolean) {
    view.inputView.isEnabled = enabled
}

@BindingAdapter("design_button_label")
internal fun setButtonLabel(view: ExtendedButtonView, label: String?) {
    view.setLabel(label)
}

@BindingAdapter("is_progress")
internal fun setButtonInProgress(view: ExtendedButtonView, isProgress: Boolean) {
    view.setProgress(isProgress)
}

@BindingAdapter(value = ["design_text", "fv"], requireAll = false)
internal fun setInputValue(view: InputView, oldText: String?, oldfv: InputFieldValue?, text: String?, fv: InputFieldValue?) {
    if (fv != null) {
        val validator = fv.validator
        val formatter = fv.formatter

        if (view.inputView.hasFocus()) {
            validator.isValidate = true
            view.setErrorText("")
        } else {
            val errorText = validator.validate(view.resources, text)
            view.setErrorText(errorText)
            validator.isValidate = false
        }

        val curText = view.inputView.text.toString()
        val newText = formatter.format(text)
        if (newText != curText) {
// TODO: при переходе через делимитер если мы не в конце строки к pos надо добавлять размер делимитера
            var pos = if (view.inputView.isEnabled)
                view.inputView.selectionStart + (newText.length - curText.length)
            else 0
            if (pos < 0) pos = 0
            if (pos > newText.length) pos = newText.length
            view.inputView.setText(newText)
            view.inputView.setSelection(pos)
        }
        fv.action?.invoke(fv.data)
    } else if (oldText != text) {
        view.inputView.setText(text)
    }
}

@InverseBindingAdapter(attribute = "design_text")
internal fun getInputValue(view: InputView) : String {
    return view.inputView.text.toString()
}

@BindingAdapter("app:design_textAttrChanged")
internal fun setInputListeners(view: InputView, attrChange: InverseBindingListener) {
    view.inputView.addTextChangedListener { _ ->
        attrChange.onChange()
    }
    view.inputView.setOnFocusChangeListener { _, _ ->
        attrChange.onChange()
    }
}

@BindingAdapter("design_label")
internal fun setTitleText(view: InputView, text: String?) {
    view.setLabel(text)
}

@BindingAdapter("design_hint")
internal fun setHintText(view: InputView, text: String?) {
    view.setHint(text)
}

@BindingAdapter("design_assistiveText")
internal fun setAssistiveText(view: InputView, text: String?) {
    view.setAssistiveText(text)
}

@BindingAdapter("error_text")
internal fun setErrorText(view: InputView, text: String?) {
    view.setErrorText(text)
}

@BindingAdapter("design_icon")
internal fun setEndDrawable(view: InputView, @DrawableRes resId: Int) {
    view.setIconResource(resId)
}

@BindingAdapter("inputType")
internal fun setInputType(view: InputView, type: String?) {
    view.inputView.inputType = when(type) {
        INPUT_TYPE_NUMBER -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
        INPUT_TYPE_NUMBER_DECIMAL -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        INPUT_TYPE_NUMBER_PASSWORD -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_NUMBER_VARIATION_NORMAL
    }
}

@BindingAdapter("adapter")
internal fun setSpinnerAdapter(view: AppCompatSpinner, adapter: ArrayAdapter<String>?) {
    view.adapter = adapter
}

@BindingAdapter(value = ["listData", "enables"], requireAll = false)
internal fun bindSpinner(view: AppCompatSpinner, data: List<String>?, enables: List<Boolean>? = null) {
    if (data != null) {
        (view.adapter as ArrayAdapter<String>).addAll(data)
        (view.adapter as? CustomSpinnerAdapter)?.enables = enables
    }
}

@BindingAdapter("selected")
internal fun setSpinnerSelectedValue(view: AppCompatSpinner, text: String?) {
    val pos = (view.adapter as? ArrayAdapter<String>)?.getPosition(text) ?: 0
    view.setSelection(pos, true)
}

@InverseBindingAdapter(attribute = "selected")
internal fun getSpinnerSelectedValue(view: AppCompatSpinner) : String {
    return view.selectedItem as String
}

@SuppressLint("ClickableViewAccessibility")
@BindingAdapter("app:selectedAttrChanged")
internal fun setSpinnerListeners(view: AppCompatSpinner, attrChange: InverseBindingListener) {
    view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            attrChange.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}

@BindingAdapter(value = ["layout_marginHorizontal", "layout_marginVertical", "layout_marginStart", "layout_height"], requireAll = false)
internal fun setLayoutMargins(view: View, @DimenRes hmRes: Int, @DimenRes vmRes: Int, @DimenRes smRes: Int, @DimenRes hRes: Int) {
    if (hmRes != 0 || vmRes != 0 || smRes != 0 || hRes != 0) {
        val hm = if (hmRes != 0) view.resources.getDimensionPixelSize(hmRes) else 0
        val vm = if (vmRes != 0) view.resources.getDimensionPixelSize(vmRes) else 0
        val sm = if (smRes != 0) view.resources.getDimensionPixelSize(smRes) else if (hm != 0) hm else 0
        val h = if (hRes != 0) view.resources.getDimensionPixelSize(hRes) else 0
        val lp = view.layoutParams as ViewGroup.MarginLayoutParams
        if (h != 0) lp.height = h
        lp.setMargins(sm, vm, hm, vm)
        view.layoutParams = lp
    }
}

@BindingAdapter("design_title")
internal fun setCellTitle(view: CellView, text: String?) {
    view.setTitle(text)
}

@BindingAdapter("design_subtitle")
internal fun setCellSubtitle(view: CellView, text: String?) {
    view.setSubtitle(text)
}

@BindingAdapter(value = ["design_drawableStart", "design_drawableEnd"], requireAll = false)
internal fun setCellDrawable(view: CellView, @DrawableRes drawableStartRes: Int, @DrawableRes drawableEndRes: Int) {
    view.setDrawableStart(drawableStartRes)
    view.setDrawableEnd(drawableEndRes)
}

@BindingAdapter(value = ["design_drawableStartTint", "design_drawableEndTint"], requireAll = false)
internal fun setCellDrawableTint(view: CellView, @ColorRes startColor: Int, @ColorRes endColor: Int) {
    if (startColor != 0)
        view.setDrawableStartTint(ColorStateList.valueOf(ContextCompat.getColor(view.context, startColor)))
    if (endColor != 0)
        view.setDrawableStartTint(ColorStateList.valueOf(ContextCompat.getColor(view.context, endColor)))
}


@BindingAdapter("background")
internal fun setCellBackground(view: CellView, @DrawableRes backgroundRes: Int) {
    view.setBackgroundResource(backgroundRes)
}

@BindingAdapter("design_title")
internal fun setCheckboxText(view: CompoundButtonView, text: String?) {
    view.setTitle(text)
}

@BindingAdapter("design_checked")
internal fun setCheckboxSelectedValue(view: CompoundButtonView, value: Boolean) {
    view.isChecked = value
}

@InverseBindingAdapter(attribute = "design_checked")
internal fun getCheckboxSelectedValue(view: CompoundButtonView) : Boolean {
    return view.isChecked
}

@BindingAdapter("app:design_checkedAttrChanged")
internal fun setCheckboxListeners(view: CompoundButtonView, attrChange: InverseBindingListener) {
    view.setOnCheckedChangeListener(object : CompoundButtonView.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButtonView, isChecked: Boolean) {
            attrChange.onChange()
        }
    })
}

@BindingAdapter("img")
internal fun setImage(view: ImageView, value: String?) {
    view.load(value)
}

@BindingAdapter("divider")
internal fun setRecyclerViewDivider(recyclerView: RecyclerView, drawable: Drawable) {
    while(recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)

    val itemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
    itemDecoration.setDrawable(drawable)
    recyclerView.addItemDecoration(itemDecoration)
}
