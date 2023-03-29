package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseBottomDialogFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Нижний диалог добавления СТС или ВУ
 */
internal class FineAddDocumentDialogFragment : BaseBottomDialogFragment<PsFragmentFormBinding, FineAddDocumentDialogViewModel>() {
    override val viewModel: FineAddDocumentDialogViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}