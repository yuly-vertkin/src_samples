package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран меню оплаты штрафов
 */
internal class FineMenuFragment : BaseFragment<PsFragmentFormBinding, FineMenuViewModel>() {
    override val viewModel: FineMenuViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}