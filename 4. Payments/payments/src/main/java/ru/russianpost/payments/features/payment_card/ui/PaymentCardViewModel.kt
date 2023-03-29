package ru.russianpost.payments.features.payment_card.ui

import androidx.lifecycle.MutableLiveData
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.METRICS_ACTION_PAYMENT_FAIL
import ru.russianpost.payments.base.ui.METRICS_ACTION_PAYMENT_SUCCESS
import ru.russianpost.payments.base.ui.METRICS_TARGET_SELF
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.PaymentCard
import ru.russianpost.payments.entities.payment_card.PaymentStatus
import ru.russianpost.payments.entities.payment_card.Receipt
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import javax.inject.Inject

/**
 * ViewModel оплаты картой
 */
internal class PaymentCardViewModel @Inject constructor(
    private val repository: PaymentCardRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    val url = MutableLiveData<String>("")

    override fun onCreate() {
        super.onCreate()

        val cardData = repository.getData()
        val card = PaymentCard(
            uin = cardData.uin,
            cardUid = cardData.cardUid,
            description = cardData.description.orEmpty(),
            redirectApprove = CARD_APPROVE_URL,
            redirectDecline = CARD_DECLINE_URL,
            redirectCancel = CARD_CANCEL_URL,
        )

        processNetworkCall(
            action = { repository.paymentCard(card) },
            onSuccess = { url.value = it.paymentReference },
            onError = { showPaymentServiceErrorDialog { actionBack.value = true }},
        )
    }

    fun onUrlLoading(loadUrl: String) : Boolean {
        when (loadUrl) {
            CARD_APPROVE_URL -> createReceipt { action.value = PaymentCardFragmentDirections.toPaymentDoneAction() }
            CARD_DECLINE_URL -> deletePaymentLink { setStatusParamAndGoBack(PaymentStatus.CARD_DECLINE) }
            CARD_CANCEL_URL -> deletePaymentLink { setStatusParamAndGoBack(PaymentStatus.CARD_CANCEL) }
            else -> { /* nothing to do */ }
        }
        return loadUrl == CARD_APPROVE_URL || loadUrl == CARD_DECLINE_URL || loadUrl == CARD_CANCEL_URL
    }

    private fun createReceipt(action: () -> Unit) {
        sendAnalyticsEvent(title.value, METRICS_TARGET_SELF, METRICS_ACTION_PAYMENT_SUCCESS)

        val receipt = Receipt(
            uin = repository.getData().uin,
            email = repository.getData().email.orEmpty(),
        )
        processNetworkCall(
            action = { repository.createReceipt(receipt) },
            onSuccess = { action() },
            onError = { action() },
        )
    }

    private fun deletePaymentLink(action: () -> Unit) {
        repository.getData().uin?.let {
            processNetworkCall(
                action = { repository.deletePaymentLink(it) },
                onSuccess = { action() },
                onError = { action() },
            )
        }
    }

    private fun setStatusParamAndGoBack(paymentStatus: PaymentStatus) {
        sendAnalyticsEvent(title.value, METRICS_TARGET_SELF, METRICS_ACTION_PAYMENT_FAIL)

        paramsRepository.saveData(paramsRepository.getData().copy(paymentStatus = paymentStatus))
        actionBack.value = true
    }

    companion object {
        const val CARD_APPROVE_URL = "android-app://ru.russianpost.payments/approve"
        const val CARD_DECLINE_URL = "android-app://ru.russianpost.payments/decline"
        const val CARD_CANCEL_URL = "android-app://ru.russianpost.payments/cancel"
    }
}