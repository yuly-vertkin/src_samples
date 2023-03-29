package ru.russianpost.payments.base.ui

import android.content.Intent
import android.net.Uri
import android.os.Handler
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import ru.russianpost.android.protocols.auth.AuthState
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.data.repositories.BaseRepository
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import javax.inject.Inject

/**
 * ViewModel главного экрана модуля Платежи
 */
internal class MainViewModel @Inject constructor(
    private val repository: ChargesRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseStartActivityViewModel(appContextProvider) {
    private var scanQRCode = false

    override fun onCreate() {
        super.onCreate()

        val authController = PaymentContract.authController
        when (authController.getAuthState()) {
            AuthState.AUTHENTICATED -> { /*checkUserChanged(<we need to get user id>)*/ }
            AuthState.NOT_AUTH -> {
                authController.initAuth()
                authController.setAuthChangedListener {
                    if (it == AuthState.AUTHENTICATED)
                        Handler(context.mainLooper).post {
                            /*checkUserChanged(<we need to get user id>)*/
                            dismissDialog.value = true
                        }
                }
                Handler(context.mainLooper).post {
                    showAuthorizationErrorDialog { actionFinish.value = true }
                }
            }
            AuthState.NOT_INIT -> showServiceUnavailableDialog{ actionFinish.value = true }
        }

        with(context.resources) {
            title.value = getString(R.string.ps_main_screen)

            addFields(listOf(
// Решили пока не отображать сканирование на главном экране
// (пока не получим обратную связь и/или пока не станет больше платежей)
/*
                CellFieldValue(
                    title = getString(R.string.ps_scan_qr_code),
                    startDrawableRes = R.drawable.ic24_commerce_qr,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onScan,
                ),
*/
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_tax,
                    title = getString(R.string.ps_tax),
                    startDrawableRes = R.drawable.ic24_finance_rouble,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onTaxClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_auto_fines,
                    title = getString(R.string.ps_auto_fines),
                    startDrawableRes = R.drawable.ic24_map_car_outline,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onAutoFinesClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section1),
                    startDrawableRes = R.drawable.ic24_finance_payment_to,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION1,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section2),
                    startDrawableRes = R.drawable.ic24_map_home_work,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION2,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section3),
                    startDrawableRes = R.drawable.ic24_device_iphone,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION3,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section4),
                    startDrawableRes = R.drawable.ic24_communication_phone_default,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION4,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section5),
                    startDrawableRes = R.drawable.ic24_postal_worldwide_processing,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION5,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section6),
                    startDrawableRes = R.drawable.ic24_device_tv,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION6,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section7),
                    startDrawableRes = R.drawable.ic24_commerce_percent,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION7,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_old_payment_section8),
                    startDrawableRes = R.drawable.ic24_action_category,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onOldPaymentClick,
                    data = OLD_PAYMENT_SECTION8,
                ),
            ))
        }
    }

    override fun onCreateView() {
        super.onCreateView()

        // clear uin
        var chargesData = repository.getData()
        chargesData = chargesData.copy(
            uin = null,
        )
        repository.saveData(chargesData)

        if (scanQRCode) {
            getAndClearBarcodeInfo()?.let {
                when {
                    it.containsKey(UID_TAX_UIN_KEY) -> handleBarcode(UID_TAX_UIN_KEY, it[UID_TAX_UIN_KEY])
                    it.containsKey(FINE_UIN_KEY) -> handleBarcode(FINE_UIN_KEY, it[FINE_UIN_KEY])
                }
            } ?: showScanErrorDialog()
            scanQRCode = false
        }

        handleStartIntent()
    }

    // TODO: for future: we'll need to clear all persist data if user changed
    private fun checkUserChanged(currentUserId: String) {
        val userId = BaseRepository.getUserIdPreference(context)
        if (userId != currentUserId) {
            BaseRepository.clearOldUserPreferences(context, currentUserId)
        }
    }

    // TODO: test only for future push handle
    private fun handleStartIntent() {
        PaymentContract.startIntent?.data?.let {
            val chargesData = repository.getData().copy(
                uin = it.getQueryParameter("uin"),
            )
            repository.saveData(chargesData)

            val startUrl = "${it.scheme}://${it.authority}${it.path}_detail_fragment"
            actionDeepLink.value = NavDeepLinkRequest.Builder
                .fromUri(startUrl.toUri())
                .build()
        }
        PaymentContract.startIntent = null
    }

    private fun getAndClearBarcodeInfo() : Map<String, String>? {
        val params = paramsRepository.getData()
        paramsRepository.saveData(params.copy(barcodeInfo = null))
        return params.barcodeInfo
    }

    private fun handleBarcode(key: String, uin: String?) {
        // save uin
        val chargesData = repository.getData().copy(
            uin = uin,
        )
        repository.saveData(chargesData)

        val url =  when(key) {
            UID_TAX_UIN_KEY -> context.resources.getString(R.string.ps_uid_tax_detail_url)
            FINE_UIN_KEY -> context.resources.getString(R.string.ps_fine_detail_url)
            else -> ""
        }

        actionDeepLink.value = NavDeepLinkRequest.Builder
            .fromUri(url.toUri())
            .build()
    }

    private fun onScan(data: Any?) {
        scanQRCode = true
        action.value = MainFragmentDirections.scanFragmentAction()
    }

    private fun onTaxClick(data: Any?) {
        action.value = MainFragmentDirections.uidTaxFragmentAction()
    }

    private fun onAutoFinesClick(data: Any?) {
        action.value = MainFragmentDirections.autoFinesFragmentAction()
    }

    override fun onMenuItem1() {
        action.value = MainFragmentDirections.historyFragmentAction()
    }

    private fun onOldPaymentClick(data: Any?) {
        data?.let {
            startOldPayment(OLD_PAYMENT_URL + it)
        }
    }

    private fun startOldPayment(urlStr: String) {
        context.startActivity(Intent().apply {
            action = Intent.ACTION_VIEW
            data = Uri.parse(urlStr)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun onActivityResult(intent: Intent?) {
    }
}