package ru.russianpost.payments.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ru.russianpost.payments.base.ui.PaymentFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
//                add<TestFragment>(R.id.fragment_container_view)
                // temp for demo
                add<PaymentFragment>(R.id.fragment_container_view)
            }
//            val intent = Intent(this, PaymentActivity::class.java)
//            startActivity(intent)
        }
    }

    override fun onRestart() {
        super.onRestart()
        finish()
    }
}