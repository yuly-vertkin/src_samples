package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран документов для поиска налогов
 */
internal class UidTaxDocumentsFragment : BaseFragment<PsFragmentFormBinding, UidTaxDocumentsViewModel>() {
    override val viewModel: UidTaxDocumentsViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}