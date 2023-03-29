package ru.russianpost.payments.features.history.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран фильтра истории платежей
 */
internal class HistoryFilterFragment : BaseMenuFragment<PsFragmentFormBinding, HistoryFilterViewModel>() {
    override val viewModel: HistoryFilterViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_filter_reset

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.showDateRangePicker.observe(viewLifecycleOwner) {
            if (it) showDateRangePickerDialog()
        }

        return result
    }

    private fun showDateRangePickerDialog() {
        val dialog = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(R.string.ps_select_period)
            .setSelection(Pair(
                MaterialDatePicker.thisMonthInUtcMilliseconds(),
                MaterialDatePicker.todayInUtcMilliseconds()
            ))
            .build()

        dialog.addOnPositiveButtonClickListener {
            viewModel.onSelectPeriod(it)
        }
        dialog.addOnCancelListener {
            viewModel.onCancelPeriod()
        }
        dialog.addOnDismissListener {
            viewModel.onCancelPeriod()
        }
        dialog.show(this.childFragmentManager, null)
    }
}