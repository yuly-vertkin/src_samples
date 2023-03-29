package ru.russianpost.payments.features.payment_card.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentViewCheckBinding
import java.io.File

/**
 * Экран показа чека
 */
internal class ViewCheckFragment : BaseFragment<PsFragmentViewCheckBinding, ViewCheckViewModel>() {
    override val viewModel: ViewCheckViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_view_check
    private val args: ViewCheckFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        with(binding) {
            pdfView.fromFile(File(args.params)).load()
        }
        return result
    }
}