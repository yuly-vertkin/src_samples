package ru.russianpost.payments.features.history.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран истории платежей
 */
@AndroidEntryPoint
internal class HistoryFragment : BaseFragment<PsFragmentFormBinding, HistoryViewModel>() {
    override val viewModel: HistoryViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        return result
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.ps_menu_history_filter, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_filter -> {
                viewModel.onActionFilter()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}