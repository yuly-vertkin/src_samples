package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран ввода информации о плательщике
 */
internal class TaxPayerInfoFragment : BaseFragment<PsFragmentFormBinding, TaxPayerInfoViewModel>() {
    override val viewModel: TaxPayerInfoViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}