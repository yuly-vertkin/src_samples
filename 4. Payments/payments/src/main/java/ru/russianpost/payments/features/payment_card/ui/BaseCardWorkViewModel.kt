package ru.russianpost.payments.features.payment_card.ui

import android.os.Bundle
import android.view.Gravity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.CardDetail
import ru.russianpost.payments.entities.payment_card.CardDetails
import ru.russianpost.payments.entities.payment_card.PaymentStatus
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.SelectCardDialogViewModel.Companion.SHOW_CARD_NUM
import ru.russianpost.payments.features.payment_card.ui.SelectCardDialogViewModel.Companion.cardBrandIcons

/**
 * Базовая ViewModel для работы с картой
 */
internal abstract class BaseCardWorkViewModel(
    protected val cardRepository: PaymentCardRepository,
    protected val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    private var cardList: List<CardDetail>? = null

    override fun onCreate() {
        super.onCreate()

        // get card list
        processNetworkCall(
            action = { cardRepository.getCards() },
            onSuccess = ::showCards,
            onError = { showCards(CardDetails()) },
            callName = CARDS_CALL_NAME,
        )
    }

    override fun onCreateView() {
        super.onCreateView()

        when(getAndClearStatusParam()) {
            PaymentStatus.CARD_DECLINE -> showPaymentCardErrorDialog()
            PaymentStatus.CARD_CANCEL -> showPaymentCardErrorDialog()
            PaymentStatus.AUTH_CANCEL -> showAuthorizationEsiaErrorDialog()
            else -> { /* nothing to do */ }
        }
    }

    private fun getAndClearStatusParam() : PaymentStatus? {
        val params = paramsRepository.getData()
        paramsRepository.saveData(params.copy(paymentStatus = null))
        return params.paymentStatus
    }

    private fun showCards(cardDetails: CardDetails) {
        cardList = cardDetails.cards

        with(context.resources) {
            addFields(listOf(
                ButtonFieldValue(
                    id = R.id.ps_button1,
                    text = MutableLiveData(getString(R.string.ps_pay_online)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    action = ::onButtonClick,
                    onIconClickAction = ::onButtonIconClick,
                ),
                TextFieldValue(
                    text = makeOfferText(R.string.ps_offer_agree, R.string.ps_offer),
                    textSize = getDimension(R.dimen.ps_text_size_12sp),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_dimen_16dp,
                    gravity = Gravity.CENTER_HORIZONTAL,
                    action = ::showAdvice,
                    data = OFFER_ADVICE,
                )
            ), isMainFields = false)
        }

        changeActiveCard(cardRepository.getData().cardUid)
    }

    protected abstract fun onButtonClick(data: Any?)

    protected abstract fun showAdvice(data: Any?)

    protected abstract fun geSelectCardAction(param: Array<CardDetail>) : NavDirections

    private fun onButtonIconClick(data: Any?) {
        cardList?.let {
            action.value = geSelectCardAction(it.toTypedArray())
        }
    }

    override fun onFragmentResult(result: Bundle) {
        val cardUid = result.getString(FRAGMENT_RESULT_KEY)
        changeActiveCard(cardUid)
    }

    private fun changeActiveCard(cardUid: String?) {
        saveCardUid(null)

        val anotherCard = context.getString(R.string.ps_another_card)
        if (cardUid == anotherCard) {
            setFieldText(R.id.ps_button1, context.getString(R.string.ps_pay_simple), isMainFields = false)
            get<ButtonFieldValue>(R.id.ps_button1, false)?.endDrawableRes?.value = R.drawable.ic24_finance_cardplaceholder_bg
            return
        }

        val cardDetail = cardList?.find { it.cardUid == cardUid } ?: cardList?.getOrNull(0)
        cardDetail?.let {
            if (it.pan.length >= SHOW_CARD_NUM) {
                val buttonTitle = context.getString(R.string.ps_pay_card, it.pan.substring(it.pan.length - SHOW_CARD_NUM))
                setFieldText(R.id.ps_button1, buttonTitle, isMainFields = false)
            }
            get<ButtonFieldValue>(R.id.ps_button1, false)?.endDrawableRes?.value =
                cardBrandIcons[it.brand] ?: 0

            saveCardUid(it.cardUid)
        }
    }

    private fun saveCardUid(cardUid: String?) {
        val cardData = cardRepository.getData()
        cardRepository.saveData(cardData.copy(cardUid = cardUid))
    }

    companion object {
        private const val CARDS_CALL_NAME = "GetCardsCall"
    }
}