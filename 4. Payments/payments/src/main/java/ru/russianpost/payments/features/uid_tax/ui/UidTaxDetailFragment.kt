package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseMenuFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран описания налога
 */
internal class UidTaxDetailFragment : BaseMenuFragment<PsFragmentFormBinding, UidTaxDetailViewModel>() {
    override val viewModel: UidTaxDetailViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    override val menuRes: Int? = null
}