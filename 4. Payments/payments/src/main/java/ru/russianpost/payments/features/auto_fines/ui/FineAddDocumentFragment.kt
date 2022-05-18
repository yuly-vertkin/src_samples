package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран добавления документа для поиска штрафов
 */
@AndroidEntryPoint
internal class FineAddDocumentFragment : BaseFragment<PsFragmentFormBinding, FineAddDocumentViewModel>() {
    override val viewModel: FineAddDocumentViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}