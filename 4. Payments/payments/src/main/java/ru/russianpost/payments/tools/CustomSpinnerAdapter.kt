package ru.russianpost.payments.tools

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import ru.russianpost.payments.R

internal class CustomSpinnerAdapter(
    context: Context,
    @LayoutRes layoutRes: Int = android.R.layout.simple_spinner_dropdown_item,
    items: List<String> = mutableListOf(),
    var enables: List<Boolean>? = null
) : ArrayAdapter<String>(context, layoutRes, items) {

    override fun getDropDownView(pos: Int, convertView: View?, parent: ViewGroup): View {
        val v = super.getDropDownView(pos, convertView, parent)
        val tv = v.findViewById<View>(android.R.id.text1) as TextView
        tv.setTextColor(if (isEnabled(pos)) ContextCompat.getColor(context, R.color.grayscale_carbon)
                        else ContextCompat.getColor(context, R.color.grayscale_plastique))
        return v
    }

    override fun isEnabled(pos: Int): Boolean {
        return enables?.get(pos) ?: true
    }
}
