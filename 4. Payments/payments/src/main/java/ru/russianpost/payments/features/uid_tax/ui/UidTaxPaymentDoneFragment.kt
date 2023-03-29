package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneFragment

/**
 * Экран результатов платежа
 */
internal class UidTaxPaymentDoneFragment : PaymentDoneFragment<PsFragmentFormBinding, UidTaxPaymentDoneViewModel>() {
    override val viewModel: UidTaxPaymentDoneViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}