package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import ru.russianpost.payments.BR
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.InjectingSavedStateViewModelFactory
import ru.russianpost.payments.tools.showSnackbar
import javax.inject.Inject

internal abstract class BaseFragment<Binding : ViewDataBinding, WM: BaseViewModel> : Fragment() {
    protected abstract val layoutRes: Int
    private var _binding: Binding? = null
    val binding
        get() = _binding!!
    protected abstract val viewModel: WM

    @Inject
    lateinit var abstractViewModelFactory: dagger.Lazy<InjectingSavedStateViewModelFactory>

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        abstractViewModelFactory.get().create(this, arguments)

    override fun onAttach(context: Context) {
        (requireActivity() as PaymentActivity).component.inject(this as BaseFragment<ViewDataBinding, BaseViewModel>)
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.onCreate()

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            viewModel.handleOnBackPressed()
            isEnabled = false
            requireActivity().onBackPressed()
        }
    }

    @SuppressLint("RedundantWith")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutRes, container, false) ?:
            throw Exception(getString(R.string.ps_error_layout_id))

        with(binding) {
            lifecycleOwner = viewLifecycleOwner
            setVariable(BR.vm, viewModel)
        }

        setFragmentResultListener(FRAGMENT_REQUEST_KEY) { requestKey, result ->
            if (requestKey == FRAGMENT_REQUEST_KEY)
                viewModel.onFragmentResult(result)
        }

        val title = (requireActivity() as AppCompatActivity).supportActionBar!!.title.toString()
        viewModel.title.value = title

        viewModel.title.observe(viewLifecycleOwner) {
            (requireActivity() as AppCompatActivity).supportActionBar!!.title = it
        }

        viewModel.onCreateView()

        viewModel.action.observe(viewLifecycleOwner) {
            it?.let { findNavController().navigate(it) }
        }
        viewModel.actionDeepLink.observe(viewLifecycleOwner) {
            it?.let {
                findNavController().navigate(it, NavOptions.Builder().setPopUpTo(R.id.mainFragment, false).build())
            }
        }
        viewModel.actionBack.observe(viewLifecycleOwner) {
            if (it) findNavController().popBackStack()
        }
        viewModel.actionFinish.observe(viewLifecycleOwner) {
            if (it) requireActivity().finish()
        }
        viewModel.showSnackbar.observe(viewLifecycleOwner) {
            it?.let { binding.root.showSnackbar(it) }
        }

        var dialog: AppCompatDialogFragment? = null

        viewModel.showDialog.observe(viewLifecycleOwner) {
            it?.let {
                dialog = SimpleDialog.show(this, it)
            }
        }
        viewModel.dismissDialog.observe(viewLifecycleOwner) {
            if (it) dialog?.dismiss()
        }
        viewModel.fragmentResult.observe(viewLifecycleOwner) {
            it?.let { setFragmentResult(FRAGMENT_REQUEST_KEY, bundleOf(FRAGMENT_RESULT_KEY to it)) }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
    }

    override fun onDestroyView() {
        viewModel.onDestroyView()
        super.onDestroyView()
        _binding = null
    }
}