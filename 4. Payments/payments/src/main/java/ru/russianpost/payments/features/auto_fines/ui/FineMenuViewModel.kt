package ru.russianpost.payments.features.auto_fines.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.FINE_UIN_KEY
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import javax.inject.Inject

/**
 * ViewModel меню оплаты штрафов
 */
internal class FineMenuViewModel @Inject constructor(
    private val repository: ChargesRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var scanQRCode = false

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    title = getString(R.string.ps_scan_qr_code),
                    startDrawableRes = R.drawable.ic24_commerce_qr,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onScan,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_fine_search_uin),
                    endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                    endDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSearchUinClick,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_fine_search_documents),
                    endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                    endDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSearchDocumentsClick,
                ),
            ))
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        // если мы отсканировали QR-код, то при возврате во фрагмент обрабатываем его
        onScanQRCode()
    }

    private fun onScanQRCode() {
        if (scanQRCode) {
            val barcodeInfo = getAndClearBarcodeInfo()
            if (barcodeInfo != null) {
                val uin = barcodeInfo[FINE_UIN_KEY]
                uin?.let {
                    val chargesData = repository.getData().copy(uin = it)
                    repository.saveData(chargesData)

                    action.value = FineMenuFragmentDirections.fineDetailFragmentAction()
                } ?: showScanErrorDialog()
            }
            scanQRCode = false
        }
    }

    private fun getAndClearBarcodeInfo() : Map<String, String>? {
        val params = paramsRepository.getData()
        paramsRepository.saveData(params.copy(barcodeInfo = null))
        return params.barcodeInfo
    }

    private fun onScan(data: Any?) {
        scanQRCode = true
        action.value = FineMenuFragmentDirections.scanFragmentAction()
    }

    private fun onSearchUinClick(data: Any?) {
        action.value = FineMenuFragmentDirections.fineUinSearchFragmentAction()
    }

    private fun onSearchDocumentsClick(data: Any?) {
        action.value = FineMenuFragmentDirections.fineSearchFragmentAction()
    }
}