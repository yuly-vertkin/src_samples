package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран описания штрафа
 */
internal class FineDetailFragment : BaseMenuFragment<PsFragmentFormBinding, FineDetailViewModel>() {
    override val viewModel: FineDetailViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes: Int? = null
}