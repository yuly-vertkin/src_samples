package ru.russianpost.payments.features.uid_tax.ui

import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран настроек налогов
 */
internal class UidTaxSettingsFragment : BaseFragment<PsFragmentFormBinding, UidTaxSettingsViewModel>() {
    override val viewModel: UidTaxSettingsViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
}