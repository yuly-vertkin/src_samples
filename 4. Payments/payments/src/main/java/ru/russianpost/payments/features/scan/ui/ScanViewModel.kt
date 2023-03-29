package ru.russianpost.payments.features.scan.ui

import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.tools.Log
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel экрана сканирования
 */
internal class ScanViewModel @Inject constructor(
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    var askedPermission = false

    fun onNoCameraPermissions() {
        showSnackbar.value = SnackbarParams(R.string.ps_no_camera_perm, style = Snackbar.Style.ERROR)
        actionBack.value = true
    }

    fun onScanResult(barcode: String) {
        val barcodeInfo = readInfoFromBarcode(barcode)
        paramsRepository.saveData(paramsRepository.getData().copy(barcodeInfo = barcodeInfo))
        actionBack.value = true
    }

    private fun readInfoFromBarcode(barcode: String?) : Map<String, String> {
        val barcodeInfo = hashMapOf<String, String>()
        barcode?.split(BARCODE_FIELDS_DELIMITER)?.forEach {
            val elem = it.split(BARCODE_ELEM_DELIMITER)
            if (elem.size == BARCODE_ELEM_SIZE) {
                barcodeInfo[elem[0]] = elem[1]
            }
        }
        return barcodeInfo
    }

    fun onErrorResult(error: Exception) {
        Log.e(error)
        showSnackbar.value = SnackbarParams(R.string.ps_scan_error, style = Snackbar.Style.ERROR)
        actionBack.value = true
    }

    fun onBackPressed() {
        actionBack.value = true
    }

    companion object {
        private const val BARCODE_FIELDS_DELIMITER = '|'
        private const val BARCODE_ELEM_DELIMITER = '='
        private const val BARCODE_ELEM_SIZE = 2
    }
}