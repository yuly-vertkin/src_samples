package ru.russianpost.payments.features.payment_card.ui

import android.content.Intent
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.BaseStartActivityViewModel
import ru.russianpost.payments.base.ui.METRICS_ACTION_TAP
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.PaymentStatus
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.tools.Log
import javax.inject.Inject

/**
 * ViewModel Авторизационного диалога
 */
internal class AuthDialogViewModel @Inject constructor(
    private val externalAuth: ExternalAuthProtocol,
    private val cardRepository: PaymentCardRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseStartActivityViewModel(appContextProvider) {

    fun onCancelClick() {
        val buttonTitle = context.getString(R.string.ps_cancel_button)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        actionBack.value = true
    }

    fun onClick() {
        val buttonTitle = context.getString(R.string.ps_auth_button)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        startIntent.value = externalAuth.createAuthIntent()
    }

    override fun onActivityResult(intent: Intent?) {
        intent?.let {
            val authResult = externalAuth.extractAuthResult(it)
            cardRepository.saveData(cardRepository.getData().copy(authResult = authResult))

            action.value = AuthDialogFragmentDirections.toConfirmationAction()
        } ?: setStatusParamAndGoBack(PaymentStatus.AUTH_CANCEL)
    }

    private fun setStatusParamAndGoBack(paymentStatus: PaymentStatus) {
        paramsRepository.saveData(paramsRepository.getData().copy(paymentStatus = paymentStatus))
        actionBack.value = true
    }
}