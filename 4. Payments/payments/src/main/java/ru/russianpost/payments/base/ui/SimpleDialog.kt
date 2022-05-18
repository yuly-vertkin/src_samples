package ru.russianpost.payments.base.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import ru.russianpost.payments.R

/** @SelfDocumented */
internal class SimpleDialog : AppCompatDialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val titleId = arguments?.getInt(TITLE_KEY) ?: 0
        val textId = arguments?.getInt(TEXT_KEY) ?: 0
        val okId = arguments?.getInt(OK_BUTTON) ?: 0
        val cancelId = arguments?.getInt(CANCEL_BUTTON) ?: 0
        val dialogBuilder = AlertDialog.Builder(requireContext())
        if (titleId != 0)
            dialogBuilder.setTitle(titleId)
        if (textId != 0)
            dialogBuilder.setMessage(textId)
        if (okId != 0)
            dialogBuilder.setPositiveButton(okId) { _, _ ->
                setFragmentResult(SIMPLE_DIALOG_REQUEST_KEY, bundleOf(SIMPLE_DIALOG_RESULT_KEY to true))
            }
        if (cancelId != 0)
            dialogBuilder.setNegativeButton(cancelId) {_, _ ->
                setFragmentResult(SIMPLE_DIALOG_REQUEST_KEY, bundleOf(SIMPLE_DIALOG_RESULT_KEY to false))
            }
        return dialogBuilder.create()
    }

// TODO: can create from layout
/*
    override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_simple_dialog, container, false)
    }
*/

    companion object {
        private const val TITLE_KEY = "title"
        private const val TEXT_KEY = "text"
        private const val OK_BUTTON = "ok"
        private const val CANCEL_BUTTON = "cancel"

        fun show(activity: AppCompatActivity, titleId: Int, textId: Int,
                 okId: Int = R.string.ps_ok_button, cancelId: Int = R.string.ps_cancel_button,
                 onCancelClick: (() -> Unit)? = null, onOkClick: () -> Unit) {
            val fragmentManager = activity.supportFragmentManager
            SimpleDialog().apply {
                arguments = bundleOf(TITLE_KEY to titleId, TEXT_KEY to textId, OK_BUTTON to okId, CANCEL_BUTTON to cancelId)
            }.show(fragmentManager, null)

            fragmentManager.setFragmentResultListener(SIMPLE_DIALOG_REQUEST_KEY, activity) { requestKey, bundle ->
                val isOk = bundle.getBoolean(SIMPLE_DIALOG_RESULT_KEY)
                if (isOk) {
                    onOkClick()
                } else onCancelClick?.let {
                    it()
                }
            }
        }

        fun show(fragment: Fragment, titleId: Int, textId: Int,
                 okId: Int = R.string.ps_ok_button, cancelId: Int = R.string.ps_cancel_button,
                 onCancelClick: (() -> Unit)? = null, onOkClick: () -> Unit) {
            val fragmentManager = fragment.childFragmentManager
            SimpleDialog().apply {
                arguments = bundleOf(TITLE_KEY to titleId, TEXT_KEY to textId, OK_BUTTON to okId, CANCEL_BUTTON to cancelId)
            }.show(fragmentManager, null)

            fragmentManager.setFragmentResultListener(SIMPLE_DIALOG_REQUEST_KEY, fragment) { requestKey, bundle ->
                val isOk = bundle.getBoolean(SIMPLE_DIALOG_RESULT_KEY)
                if (isOk) {
                    onOkClick()
                } else onCancelClick?.let {
                    it()
                }
            }
        }
    }
}