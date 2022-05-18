package ru.russianpost.payments.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.russianpost.payments.BR
import ru.russianpost.payments.R
import ru.russianpost.payments.tools.showSnackbar

internal abstract class BaseFragment<Binding : ViewDataBinding, WM: BaseViewModel> : Fragment() {
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

        viewModel.title.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                (requireActivity() as AppCompatActivity).supportActionBar!!.title = it
            }
        }

        viewModel.onCreateView()

        viewModel.action.observe(viewLifecycleOwner) {
            it?.let { findNavController().navigate(it) }
        }
        viewModel.actionBack.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
        viewModel.showSnackbar.observe(viewLifecycleOwner) {
            it?.let { binding.root.showSnackbar(it) }
        }
        viewModel.showDialog.observe(viewLifecycleOwner) {
            it?.let {
                when(it) {
                    DialogTypes.SERVICE_UNAVAILABLE -> showServiceUnavailableDialog()
                    DialogTypes.PAYMENT_ERROR -> showPaymentErrorDialog()
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showServiceUnavailableDialog() {
        SimpleDialog.show(this,
            titleId = R.string.ps_error_service_unavailable,
            textId = R.string.ps_error_try_later,
            okId = R.string.ps_understand,
            cancelId = 0
        ) {}
    }

    private fun showPaymentErrorDialog() {
        SimpleDialog.show(this,
            titleId = R.string.ps_error_payment_title,
            textId = R.string.ps_error_payment_text,
            okId = R.string.ps_understand,
            cancelId = 0
        ) {}
    }
}