package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран добавления документа для поиска налогов
 */
internal class UidTaxAddDocumentFragment : BaseFragment<PsFragmentFormBinding, UidTaxAddDocumentViewModel>() {
    override val viewModel: UidTaxAddDocumentViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}