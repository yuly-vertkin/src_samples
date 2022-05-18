package ru.russianpost.payments.base.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

/**
 * Fragment для вызова из приложения
 */
class PaymentFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(requireContext(), PaymentActivity::class.java)
        startActivity(intent)
    }
}