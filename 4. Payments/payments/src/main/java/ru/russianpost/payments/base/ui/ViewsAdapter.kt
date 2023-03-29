package ru.russianpost.payments.base.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.*

internal enum class ViewType {
//    BANNER,
//    BUTTON,
    BUTTON_TOGGLE,
    EXTENDED_BUTTON,
    TEXT,
    IMAGE,
    CELL,
    CELL_VALUE,
//    CELL_MULTI,
    CHECKBOX,
    CHECKBOX_RIGHT,
    INPUT,
//    SWITCH_ITEM,
//    RADIO_BUTTON,
    EMPTY_VIEW,
    SPINNER,
    AUTO_COMPLETE_TEXT,
    DIVIDER,
    CONTAINER,
    AUTO_FINE,
    HISTORY,
}

internal class ViewHolder<Binding : ViewDataBinding>(
    resId: Int,
    val parent: ViewGroup,
    var binding: Binding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), resId, parent, false)
) : RecyclerView.ViewHolder(binding.root) {

    init {
        val fragment = parent.findFragment<Fragment>()

        with(binding) {
            lifecycleOwner = fragment.viewLifecycleOwner
        }
    }

    fun bind(item: BaseFieldValue) {
        item.bind(parent, binding)
    }
}

internal fun createViewHolderByType(parent: ViewGroup, viewType: Int): ViewHolder<out ViewDataBinding> {
    val viewHolder = when (viewType) {
        ViewType.INPUT.ordinal -> ViewHolder<PsItemInputViewBinding>(R.layout.ps_item_input_view, parent)
        ViewType.SPINNER.ordinal -> ViewHolder<PsItemSpinnerViewBinding>(R.layout.ps_item_spinner_view, parent)
        ViewType.AUTO_COMPLETE_TEXT.ordinal -> ViewHolder<PsItemAutoCompleteTextViewBinding>(R.layout.ps_item_auto_complete_text_view, parent)
        ViewType.CHECKBOX.ordinal -> ViewHolder<PsItemCheckboxViewBinding>(R.layout.ps_item_checkbox_view, parent)
        ViewType.CHECKBOX_RIGHT.ordinal -> ViewHolder<PsItemCheckboxRightViewBinding>(R.layout.ps_item_checkbox_right_view, parent)
        ViewType.BUTTON_TOGGLE.ordinal -> ViewHolder<PsItemButtonTogleViewBinding>(R.layout.ps_item_button_togle_view, parent)
        ViewType.EXTENDED_BUTTON.ordinal -> ViewHolder<PsItemExtendedButtonViewBinding>(R.layout.ps_item_extended_button_view, parent)
        ViewType.CELL.ordinal -> ViewHolder<PsItemCellViewBinding>(R.layout.ps_item_cell_view, parent)
        ViewType.CELL_VALUE.ordinal -> ViewHolder<PsItemValueCellViewBinding>(R.layout.ps_item_value_cell_view, parent)
        ViewType.EMPTY_VIEW.ordinal -> ViewHolder<PsItemEmptyViewBinding>(R.layout.ps_item_empty_view, parent)
        ViewType.TEXT.ordinal -> ViewHolder<PsItemTextViewBinding>(R.layout.ps_item_text_view, parent)
        ViewType.IMAGE.ordinal -> ViewHolder<PsItemImageViewBinding>(R.layout.ps_item_image_view, parent)
        ViewType.DIVIDER.ordinal -> ViewHolder<PsItemDividerViewBinding>(R.layout.ps_item_divider_view, parent)
        ViewType.CONTAINER.ordinal -> ViewHolder<PsItemContainerViewBinding>(R.layout.ps_item_container_view, parent)
        ViewType.AUTO_FINE.ordinal -> ViewHolder<PsItemChargeViewBinding>(R.layout.ps_item_charge_view, parent)
        ViewType.HISTORY.ordinal -> ViewHolder<PsItemHistoryViewBinding>(R.layout.ps_item_history_view, parent)
        else -> throw IllegalStateException()
    }
    return viewHolder
}

internal class ViewsAdapter : ListAdapter<BaseFieldValue, ViewHolder<out ViewDataBinding>>(DiffCalback) {
    object DiffCalback : DiffUtil.ItemCallback<BaseFieldValue>() {
        override fun areItemsTheSame(oldItem: BaseFieldValue, newItem: BaseFieldValue): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BaseFieldValue, newItem: BaseFieldValue): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<out ViewDataBinding> {
        return createViewHolderByType(parent, viewType)
    }

    override fun onBindViewHolder(holder: ViewHolder<out ViewDataBinding>, position: Int) {
        holder.bind(getItem(position))
    }
}
