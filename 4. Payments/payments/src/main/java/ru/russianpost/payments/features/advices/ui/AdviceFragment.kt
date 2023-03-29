package ru.russianpost.payments.features.advices.ui

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseBottomDialogFragment
import ru.russianpost.payments.databinding.PsFragmentFormBinding

/**
 * Экран подсказки
 */
internal class AdviceFragment : BaseBottomDialogFragment<PsFragmentFormBinding, AdviceViewModel>() {
    override val viewModel: AdviceViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_form
    private val args: AdviceFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.setStartParams(args.params)
    }
}