package ru.russianpost.payments.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.ViewDataBinding

internal abstract class BaseStartActivityFragment<Binding : ViewDataBinding, WM: BaseStartActivityViewModel> : BaseMenuFragment<Binding, WM>() {
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onActivityResult(it.data)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.startIntent.observe(viewLifecycleOwner) {
            startForResult.launch(it)
        }

        return result
    }
}