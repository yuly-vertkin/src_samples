package ru.russianpost.payments.base.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import ru.russianpost.payments.R

internal abstract class BaseMenuFragment<Binding : ViewDataBinding, WM: BaseMenuViewModel> : BaseFragment<Binding, WM>() {
    protected abstract val menuRes: Int?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
                menuRes?.let {
                    requireActivity().menuInflater.inflate(it, menu)
                }
            }

            override fun onMenuItemSelected(item: MenuItem): Boolean {
                when(item.itemId) {
                    R.id.menu_item1 -> viewModel.onMenuItem1()
                    else -> {}
                }
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }
}