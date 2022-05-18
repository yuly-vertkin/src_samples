package ru.russianpost.payments.features.auto_fines.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран списка штрафов по СТС и ВУ
 */
@AndroidEntryPoint
internal class FinesFragment : BaseFragment<PsFragmentFormBinding, FinesViewModel>() {
    override val viewModel: FinesViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        setHasOptionsMenu(true)

        return result
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        requireActivity().menuInflater.inflate(R.menu.ps_menu_settings, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> {
                viewModel.onActionSettings()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}