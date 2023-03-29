package ru.russianpost.payments.features.auto_fines.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneFragment

/**
 * Экран результатов платежа
 */
internal class FinePaymentDoneFragment : PaymentDoneFragment<PsFragmentFormBinding, FinePaymentDoneViewModel>() {
    override val viewModel: FinePaymentDoneViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}