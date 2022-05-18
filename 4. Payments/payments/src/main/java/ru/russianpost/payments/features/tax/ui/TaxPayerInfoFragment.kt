package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран ввода информации о плательщике
 */
@AndroidEntryPoint
internal class TaxPayerInfoFragment : BaseFragment<PsFragmentFormBinding, TaxPayerInfoViewModel>() {
    override val viewModel: TaxPayerInfoViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}