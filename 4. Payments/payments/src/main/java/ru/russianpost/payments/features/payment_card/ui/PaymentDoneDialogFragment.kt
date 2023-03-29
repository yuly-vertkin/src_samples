package ru.russianpost.payments.features.payment_card.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseBottomDialogFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Нижний диалог фрагмент
 */
internal class PaymentDoneDialogFragment : BaseBottomDialogFragment<PsFragmentFormBinding, PaymentDoneDialogViewModel>() {
    override val viewModel: PaymentDoneDialogViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}