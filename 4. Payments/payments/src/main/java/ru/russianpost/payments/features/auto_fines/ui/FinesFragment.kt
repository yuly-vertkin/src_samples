package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран списка штрафов по СТС и ВУ
 */
internal class FinesFragment : BaseMenuFragment<PsFragmentFormBinding, FinesViewModel>() {
    override val viewModel: FinesViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_settings
}