package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран редактирования документа для поиска налогов
 */
internal class UidTaxEditDocumentFragment : BaseMenuFragment<PsFragmentFormBinding, UidTaxEditDocumentViewModel>() {
    override val viewModel: UidTaxEditDocumentViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_delete
}