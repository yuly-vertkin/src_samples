package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран ввода информации о платеже
 */
internal class TaxPaymentInfoFragment : BaseMenuFragment<PsFragmentFormBinding, TaxPaymentInfoViewModel>() {
    override val viewModel: TaxPaymentInfoViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes: Int? = null
}