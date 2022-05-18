package ru.russianpost.payments.features.tax.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран ввода информации о получателе
 */
@AndroidEntryPoint
internal class TaxRecipientInfoFragment : BaseFragment<PsFragmentFormBinding, TaxRecipientInfoViewModel>() {
    override val viewModel: TaxRecipientInfoViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}