package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment

/**
 * Fragment для вызова из приложения
 */
class PaymentFragment : Fragment(/*R.layout.ps_fragment_payment_test*/) {

    private val startForResult = registerForActivityResult(StartActivityForResult()) {
        parentFragmentManager.popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        startForResult.launch(intent)
//        startActivity(intent)
    }

    companion object {
        fun getPaymentActivityIntent(context: Context) = Intent(context, PaymentActivity::class.java)
    }
}