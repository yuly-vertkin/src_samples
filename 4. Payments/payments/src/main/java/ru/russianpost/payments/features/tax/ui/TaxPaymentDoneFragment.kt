package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentFormBinding
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneFragment

/**
 * Экран результатов платежа
 */
internal class TaxPaymentDoneFragment : PaymentDoneFragment<PsFragmentFormBinding, TaxPaymentDoneViewModel>() {
    override val viewModel: TaxPaymentDoneViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}