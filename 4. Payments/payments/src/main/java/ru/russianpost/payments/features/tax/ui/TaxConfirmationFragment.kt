package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран подтверждения платежа
 */
internal class TaxConfirmationFragment : BaseFragment<PsFragmentFormBinding, TaxConfirmationViewModel>() {
    override val viewModel: TaxConfirmationViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}