package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран поиска штрафа по УИН
 */
internal class FineUinSearchFragment : BaseFragment<PsFragmentFormBinding, FineUinSearchViewModel>() {
    override val viewModel: FineUinSearchViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}