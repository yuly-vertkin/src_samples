package ru.russianpost.payments.features.scan.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import ru.russianpost.barcodescannerview.*
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentScanBinding
import ru.russianpost.payments.tools.SnackbarParams
import ru.russianpost.payments.tools.showSnackbar

/**
 * Экран сканирования
 */
internal class ScanFragment : BaseFragment<PsFragmentScanBinding, ScanViewModel>() {
    override val viewModel: ScanViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_scan

    /** Листнер для обработки события запроса на доступ к камере */
    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> binding.barcodeScanner.onResume()
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> checkCameraPermission()
                else -> viewModel.onNoCameraPermissions()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.barcodeScanner.apply {
            setBeepManager(requireActivity())

            scanListener = object : BarcodeScannerScanListener {
                override fun onScanResult(barcode: String) =
                    viewModel.onScanResult(barcode)
            }

            torchListener = object : BarcodeScannerTorchListener {
                override fun torchIsOn() =
                    requireView().showSnackbar(SnackbarParams(R.string.ps_torch_on))

                override fun torchIsOff() =
                    requireView().showSnackbar(SnackbarParams(R.string.ps_torch_off))
            }

            errorListener = object : BarcodeScannerErrorListener {
                override fun onErrorResult(error: Exception) =
                    viewModel.onErrorResult(error)
            }

            backListener = object : BarcodeScannerBackButtonListener {
                override fun onBackPressed() =
                    viewModel.onBackPressed()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkCameraPermission()
        } else {
            binding.barcodeScanner.onResume()
            startScanning()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.barcodeScanner.onPause()
    }

    private fun startScanning() {
        binding.barcodeScanner.restartDecoding(DECODE_MODE)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            binding.barcodeScanner.onResume()
            startScanning()
        } else if (!viewModel.askedPermission) {
            viewModel.askedPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    companion object {
        private val DECODE_MODE: BarcodeScannerScanMode = BarcodeScannerScanMode.SINGLE
    }
}