package ru.russianpost.payments.features.history.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран истории платежей
 */
internal class HistoryFragment : BaseMenuFragment<PsFragmentFormBinding, HistoryViewModel>() {
    override val viewModel: HistoryViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_history_filter
}