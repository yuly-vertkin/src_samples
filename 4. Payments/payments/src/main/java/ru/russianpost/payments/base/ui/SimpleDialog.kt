package ru.russianpost.payments.base.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.russianpost.payments.R

internal data class SimpleDialogParams(
    val title: String,
    val text: String,
    val ok: String,
    val cancel: String = "",
    val isCancelable: Boolean = true,
    val onCancelClick: (() -> Unit)? = null,
    val onOkClick: (() -> Unit)? = null,
)

/** @SelfDocumented */
internal class SimpleDialog : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments?.getString(TITLE_KEY)
        val text = arguments?.getString(TEXT_KEY)
        val ok = arguments?.getString(OK_BUTTON).orEmpty()
        val cancel = arguments?.getString(CANCEL_BUTTON).orEmpty()

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
            .setTitle(title)
            .setMessage(text)

// внутри стандартного диалога можно добавлять свои поля
//        val inflater: LayoutInflater = LayoutInflater.from(requireContext())
//        val promptsView = inflater.inflate(R.layout.ps_fragment_input_dialog, null)
//        dialogBuilder.setView(promptsView)

        if (ok.isNotEmpty())
            dialogBuilder.setPositiveButton(ok) { _, _ ->
                setFragmentResult(SIMPLE_DIALOG_REQUEST_KEY, bundleOf(SIMPLE_DIALOG_RESULT_KEY to true))
            }
        if (cancel.isNotEmpty())
            dialogBuilder.setNegativeButton(cancel) { _, _ ->
                setFragmentResult(SIMPLE_DIALOG_REQUEST_KEY, bundleOf(SIMPLE_DIALOG_RESULT_KEY to false))
            }

        val dialog = dialogBuilder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

// can create from layout
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return inflater.inflate(R.layout.ps_fragment_simple_dialog, container, false)
//    }

    companion object {
        private const val TITLE_KEY = "title"
        private const val TEXT_KEY = "text"
        private const val OK_BUTTON = "ok"
        private const val CANCEL_BUTTON = "cancel"

        fun show(activity: AppCompatActivity, params: SimpleDialogParams) : AppCompatDialogFragment {
            val fragmentManager = activity.supportFragmentManager
            val dialog = SimpleDialog().apply {
                arguments = bundleOf(TITLE_KEY to params.title, TEXT_KEY to params.text, OK_BUTTON to params.ok, CANCEL_BUTTON to params.cancel)
            }
            dialog.isCancelable = params.isCancelable
            dialog.show(fragmentManager, null)

            fragmentManager.setFragmentResultListener(SIMPLE_DIALOG_REQUEST_KEY, activity) { requestKey, bundle ->
                val isOk = bundle.getBoolean(SIMPLE_DIALOG_RESULT_KEY)
                if (isOk) params.onOkClick?.invoke()
                else params.onCancelClick?.invoke()
            }
            return dialog
        }

        fun show(fragment: Fragment, params: SimpleDialogParams) : AppCompatDialogFragment {
            val fragmentManager = fragment.childFragmentManager
            val dialog = SimpleDialog().apply {
                arguments = bundleOf(TITLE_KEY to params.title, TEXT_KEY to params.text, OK_BUTTON to params.ok, CANCEL_BUTTON to params.cancel)
            }
            dialog.isCancelable = params.isCancelable
            dialog.show(fragmentManager, null)

            fragmentManager.setFragmentResultListener(SIMPLE_DIALOG_REQUEST_KEY, fragment) { requestKey, bundle ->
                val isOk = bundle.getBoolean(SIMPLE_DIALOG_RESULT_KEY)
                if (isOk) params.onOkClick?.invoke()
                else params.onCancelClick?.invoke()
            }
            return dialog
        }
    }
}