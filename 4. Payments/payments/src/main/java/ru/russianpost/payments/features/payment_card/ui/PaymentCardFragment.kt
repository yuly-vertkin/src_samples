package ru.russianpost.payments.features.payment_card.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран оплаты картой
 */
@AndroidEntryPoint
internal class PaymentCardFragment : BaseFragment<PsFragmentFormBinding, PaymentCardViewModel>() {
    override val viewModel: PaymentCardViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}