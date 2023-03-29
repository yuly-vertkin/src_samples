package ru.russianpost.payments.base.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Главный экран модуля Платежи
 */
internal class MainFragment : BaseStartActivityFragment<PsFragmentFormBinding, MainViewModel>() {
    override val viewModel: MainViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_history
}