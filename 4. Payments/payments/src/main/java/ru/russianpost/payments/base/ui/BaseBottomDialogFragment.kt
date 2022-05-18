package ru.russianpost.payments.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.russianpost.payments.BR
import ru.russianpost.payments.R

internal abstract class BaseBottomDialogFragment<Binding : ViewDataBinding, WM: BaseViewModel> : BottomSheetDialogFragment() {
    protected abstract val layoutRes: Int
    private var _binding: Binding? = null
    val binding
        get() = _binding!!
    protected abstract val viewModel: WM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false) ?:
            throw Exception(getString(R.string.ps_error_layout_id))

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            binding.setVariable(BR.vm, viewModel)
        }

        viewModel.onCreateView()
        setRvHeight(binding.root)

        viewModel.action.observe(viewLifecycleOwner) {
            it?.let { findNavController().navigate(it) }
        }
        viewModel.actionBack.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }

        return binding.root
    }

    private fun setRvHeight(view: View) {
        val rv = (view as ViewGroup).getChildAt(0)
        val lp = rv.layoutParams as ViewGroup.MarginLayoutParams
        lp.height = viewModel.getFieldSize() * resources.getDimensionPixelSize(R.dimen.ps_cell_height)
        rv.layoutParams = lp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}