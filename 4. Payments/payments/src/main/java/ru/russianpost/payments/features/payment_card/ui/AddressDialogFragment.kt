package ru.russianpost.payments.features.payment_card.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseDialogFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Диалог ввода адреса плательщика
 */
internal class AddressDialogFragment : BaseDialogFragment<PsFragmentFormBinding, AddressDialogViewModel>() {
    override val viewModel: AddressDialogViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}