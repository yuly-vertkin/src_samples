package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран редактирования документа для поиска штрафов
 */
internal class FineEditDocumentFragment : BaseMenuFragment<PsFragmentFormBinding, FineEditDocumentViewModel>() {
    override val viewModel: FineEditDocumentViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes = R.menu.ps_menu_delete
}