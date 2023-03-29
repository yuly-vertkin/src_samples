package ru.russianpost.payments.features.payment_card.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseStartActivityFragment
import ru.russianpost.payments.base.ui.PaymentActivity
import ru.russianpost.payments.databinding.PsFragmentAuthDialogBinding

/**
 * Авторизационный диалог
 */
internal class AuthDialogFragment : BaseStartActivityFragment<PsFragmentAuthDialogBinding, AuthDialogViewModel>() {
    override val viewModel: AuthDialogViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_auth_dialog
    override val menuRes: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        (requireActivity() as PaymentActivity).toolbar.visibility = GONE
        return result
    }

    override fun onDestroyView() {
        (requireActivity() as PaymentActivity).toolbar.visibility = VISIBLE

        super.onDestroyView()
    }
}