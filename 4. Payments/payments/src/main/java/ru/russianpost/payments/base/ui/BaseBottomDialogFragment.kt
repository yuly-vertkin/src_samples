package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.russianpost.payments.BR
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.InjectingSavedStateViewModelFactory
import javax.inject.Inject

internal abstract class BaseBottomDialogFragment<Binding : ViewDataBinding, WM: BaseViewModel> : BottomSheetDialogFragment() {
    protected abstract val layoutRes: Int
    private var _binding: Binding? = null
    val binding
        get() = _binding!!
    protected abstract val viewModel: WM

    @Inject
    lateinit var abstractViewModelFactory: dagger.Lazy<InjectingSavedStateViewModelFactory>

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        abstractViewModelFactory.get().create(this)

    override fun onAttach(context: Context) {
        (requireActivity() as PaymentActivity).component.inject(this as BaseBottomDialogFragment<ViewDataBinding, BaseViewModel>)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onCreate()
    }

    @SuppressLint("RedundantWith")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false) ?:
            throw Exception(getString(R.string.ps_error_layout_id))

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            setVariable(BR.vm, viewModel)
        }

        viewModel.onCreateView()
        setRvHeight(binding.root)

        viewModel.action.observe(viewLifecycleOwner) {
            it?.let { findNavController().navigate(it) }
        }
        viewModel.actionBack.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
        viewModel.fragmentResult.observe(viewLifecycleOwner) {
            it?.let { setFragmentResult(FRAGMENT_REQUEST_KEY, bundleOf(FRAGMENT_RESULT_KEY to it)) }
        }

        return binding.root
    }

    private fun setRvHeight(view: View) {
        val rv = (view as ViewGroup).getChildAt(0)
        val lp = rv.layoutParams as ViewGroup.MarginLayoutParams
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        rv.layoutParams = lp
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}