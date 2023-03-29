package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран документов для поиска штрафов
 */
internal class FineDocumentsFragment : BaseFragment<PsFragmentFormBinding, FineDocumentsViewModel>() {
    override val viewModel: FineDocumentsViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}