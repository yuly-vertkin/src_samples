package ru.russianpost.payments.base.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран подсказки
 */
@AndroidEntryPoint
internal class AdviceFragment : BaseFragment<PsFragmentFormBinding, AdviceViewModel>() {
    override val viewModel: AdviceViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}