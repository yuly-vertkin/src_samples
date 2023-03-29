package ru.russianpost.payments.features.uid_tax.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.UID_TAX_UIN_KEY
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import javax.inject.Inject

/**
 * ViewModel меню оплаты налогов
 */
internal class UidTaxMenuViewModel @Inject constructor(
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
                    title = getString(R.string.ps_uid_tax_search_documents),
                    endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                    endDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSearchDocumentsClick,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_uid_tax_search_uin),
                    endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                    endDrawableColorRes = R.color.grayscale_stone,
                    action = ::onSearchUinClick,
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
                val uin = barcodeInfo[UID_TAX_UIN_KEY]
                uin?.let {
                    val chargesData = repository.getData().copy(uin = it)
                    repository.saveData(chargesData)

                    action.value = UidTaxMenuFragmentDirections.uidTaxDetailFragmentAction()
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
        action.value = UidTaxMenuFragmentDirections.scanFragmentAction()
    }

    private fun onSearchUinClick(data: Any?) {
        action.value = UidTaxMenuFragmentDirections.uidTaxUinSearchFragmentAction()
    }

    private fun onSearchDocumentsClick(data: Any?) {
        action.value = UidTaxMenuFragmentDirections.uidTaxSearchFragmentAction()
    }
}