package ru.russianpost.payments.features.payment_card.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.ViewDataBinding
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.base.ui.SimpleDialog
import ru.russianpost.payments.base.ui.SimpleDialogParams
import ru.russianpost.payments.base.ui.WRITE_EXTERNAL_STORAGE_PERMISSION
import ru.russianpost.payments.tools.SnackbarParams
import ru.russianpost.payments.tools.showSnackbar
import java.io.File

/**
 * Экран результатов платежа
 */
internal abstract class PaymentDoneFragment<Binding : ViewDataBinding, WM: PaymentDoneViewModel> : BaseFragment<Binding, WM>() {
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { onRequestPermission(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val result = super.onCreateView(inflater, container, savedInstanceState)

        viewModel.onSaveAction.observe(viewLifecycleOwner) {
            if (it) getWriteExternalStoragePermission()
        }
        viewModel.onShareAction.observe(viewLifecycleOwner) {
            it?.let { shareAction(it) }
        }

        return result
    }

    private fun getWriteExternalStoragePermission() {
        when {
            ContextCompat.checkSelfPermission(requireContext().applicationContext, WRITE_EXTERNAL_STORAGE_PERMISSION) ==
            PackageManager.PERMISSION_GRANTED -> onRequestPermission(true)

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE_PERMISSION) -> onPermissionRationale()

            else -> requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE_PERMISSION)
        }
    }

    private fun onPermissionRationale() {
        SimpleDialog.show( this, SimpleDialogParams(
            title = getString(R.string.ps_write_file_permission_title),
            text = getString(R.string.ps_write_file_permission_text),
            ok = getString(R.string.ps_ok_button),
            cancel = getString(R.string.ps_cancel_button),
            onCancelClick = { onRequestPermission(false) }
        ) {
            requestPermissionLauncher.launch(WRITE_EXTERNAL_STORAGE_PERMISSION)
        })
    }

    private fun onRequestPermission(isGranted: Boolean) {
        when {
            isGranted -> viewModel.onSaveCheck(this, isPermissionGranted = true)
            else -> requireView().showSnackbar(SnackbarParams(R.string.ps_error_get_download_dir))
        }
    }

    private fun shareAction(appPath: String) {
        val appFile = File(appPath)

        val fileUri: Uri = try {
            FileProvider.getUriForFile(
                requireContext(),
                requireContext().packageName + ".payments.provider",
                appFile)
        } catch (e: IllegalArgumentException) {
            requireView().showSnackbar(SnackbarParams(R.string.ps_error_sharing))
            return
        }

        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "application/pdf"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)
        startActivity(share)
    }
}