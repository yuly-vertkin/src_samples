package ru.russianpost.payments.features.payment_card.ui

import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.base.ui.CellFieldValue
import ru.russianpost.payments.base.ui.DividerFieldValue
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.payment_card.CardDetail
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import javax.inject.Inject

/**
 * ViewModel нижнего диалога выбора карты для оплаты
 */
internal class SelectCardDialogViewModel @Inject constructor(
    private val cardRepository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    fun setStartParams(cardDetails: Array<CardDetail>) {
        val cardUid = cardRepository.getData().cardUid
        cardDetails.forEach {
            addFields(listOf(
                CellFieldValue(
                    title = if (it.pan.length >= SHOW_CARD_NUM) it.pan.substring(it.pan.length - SHOW_CARD_NUM) else it.pan,
                    startDrawableRes = cardBrandIcons[it.brand] ?: 0,
                    endDrawableRes = if (it.cardUid == cardUid) R.drawable.ic24_action_done else 0,
                    endDrawableColorRes = R.color.grayscale_carbon,
                    action = ::onSelectCard,
                    data = it.cardUid,
                ),
                DividerFieldValue(
                    heightRes = R.dimen.ps_zero_height,
                ),
            ))
        }
        val anotherCard = context.getString(R.string.ps_another_card)
        addField(
            CellFieldValue(
                title = anotherCard,
                startDrawableRes = R.drawable.ic24_finance_payment,
                endDrawableRes = if (cardUid == null) R.drawable.ic24_action_done else 0,
                endDrawableColorRes = R.color.grayscale_carbon,
                action = ::onSelectCard,
                data = anotherCard,
            ),
        )
    }

    private fun onSelectCard(data: Any?) {
        val cardUid = data as? String
        fragmentResult.value = cardUid
        actionBack.value = true
    }

    companion object {
        const val SHOW_CARD_NUM = 6
        val cardBrandIcons = mapOf<String, Int>("VISA" to R.drawable.ic24_logo_visa_active_bg, "MASTER" to R.drawable.ic24_logo_mastercard_active_bg)
    }
}