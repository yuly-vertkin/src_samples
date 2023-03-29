package ru.russianpost.payments.features.payment_card.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseBottomDialogFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Нижний диалог выбора карты для оплаты
 */
internal class SelectCardDialogFragment : BaseBottomDialogFragment<PsFragmentFormBinding, SelectCardDialogViewModel>() {
    override val viewModel: SelectCardDialogViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    private val args: SelectCardDialogFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setStartParams(args.params)
    }
}