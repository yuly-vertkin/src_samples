package ru.russianpost.payments.demo

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import ru.russianpost.payments.base.ui.PaymentFragment

class TestFragment : Fragment(R.layout.fragment_test) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().findViewById<Button>(R.id.test).setOnClickListener {
            parentFragmentManager.commit {
                setReorderingAllowed(true)
                add<PaymentFragment>(R.id.fragment_container_view)
                addToBackStack(null)
            }
        }

    }
}