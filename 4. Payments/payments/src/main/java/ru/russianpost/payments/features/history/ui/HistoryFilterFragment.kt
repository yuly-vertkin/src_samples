package ru.russianpost.payments.features.history.ui

import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран фильтра истории платежей
 */
@AndroidEntryPoint
internal class HistoryFilterFragment : BaseFragment<PsFragmentFormBinding, HistoryFilterViewModel>() {
    override val viewModel: HistoryFilterViewModel by viewModels()
    override val layoutRes
        get() = R.layout.ps_fragment_form
}