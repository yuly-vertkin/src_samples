package ru.russianpost.payments.base.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Главный экран модуля Платежи
 */
@AndroidEntryPoint
internal class MainFragment : BaseFragment<PsFragmentFormBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        return result
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.ps_menu_history, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_history -> {
                viewModel.onActionHistory()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}