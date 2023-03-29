package ru.russianpost.payments.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.russianpost.payments.PaymentContract

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
/*
            supportFragmentManager.commit {
                setReorderingAllowed(true)
//                add<TestFragment>(R.id.fragment_container_view)
//                addToBackStack(null)
                // for demo
                add<PaymentFragment>(R.id.fragment_container_view)
            }
*/
            startPaymentActivity()
        }
    }

    private fun startPaymentActivity() {
        val paymentIntent = PaymentContract.getPaymentActivityIntent(this).also {
            it.data = intent.data
        }
        startActivity(paymentIntent)
    }

    // for demo
    override fun onRestart() {
        super.onRestart()
        finish()
    }
}