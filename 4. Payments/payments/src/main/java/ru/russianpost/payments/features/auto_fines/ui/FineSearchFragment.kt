package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран поиска штрафов по СТС и ВУ
 */
@AndroidEntryPoint
internal class FineSearchFragment : BaseFragment<PsFragmentFormBinding, FineSearchViewModel>() {
    override val viewModel: FineSearchViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}