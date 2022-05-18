package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран ввода информации о платеже
 */
@AndroidEntryPoint
internal class TaxPaymentInfoFragment : BaseFragment<PsFragmentFormBinding, TaxPaymentInfoViewModel>() {
    override val viewModel: TaxPaymentInfoViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}