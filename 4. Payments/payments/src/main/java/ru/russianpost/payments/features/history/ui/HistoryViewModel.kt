package ru.russianpost.payments.features.history.ui

import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.PaymentStartParams
import ru.russianpost.payments.entities.history.History
import ru.russianpost.payments.entities.history.PaymentType
import ru.russianpost.payments.features.history.domain.HistoryRepository
import ru.russianpost.payments.tools.formatReverseDate
import ru.russianpost.payments.tools.getDateFromStr
import ru.russianpost.payments.tools.getMonthFromDate
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel истории платежей
 */
internal class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {

    // we need reload history after changing history filter
    // so make network call in onCreateView()
    override fun onCreateView() {
        super.onCreateView()

// back microservice is not available yet
/*
        processNetworkCall(
            action = { repository.getHistory() },
            onSuccess = ::addHistory,
            onError = { showServiceUnavailableDialog() },
        )
*/
        addHistory(listOf(History("123", PaymentType.PAYMENT.name, "purpose", 1.0f, "2022-12-12")))
    }

    private fun addHistory(history: List<History>) {
        if (history.isEmpty()) {
            with(context.resources) {
                addField(
                    TextFieldValue(
                        id = R.id.ps_fines_not_found,
                        text = getString(R.string.ps_history_not_found),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    )
                )
            }
        }

        var lastDate = ""
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        history.sortedByDescending {
            getDateFromStr(it.createdAt, inputFormat)?.time ?: 0
        }.map {
            val currentDay = getMonthFromDate(it.createdAt)
            if (currentDay != lastDate) {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = if (lastDate != "") R.dimen.ps_big_divider_height else R.dimen.ps_zero_height,
                    ),
                    TextFieldValue(
                        text = currentDay,
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = context.resources.getDimension(R.dimen.ps_text_size_16sp),
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    )
                ))
                lastDate = currentDay
            }
            addFields(listOf(
                HistoryFieldValue(
                    title = getTitle(it.type),
                    date = formatReverseDate(it.createdAt),
                    sum = makeSum(it.totalAmount),
                    details = it.purpose,
                    data = it,
                    action = ::onHistoryClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                )
            ))
        }
    }

    private fun getTitle(type: String?) =
        when(type) {
            PaymentType.PAYMENT.name, PaymentType.TAX_PAYMENT.name -> context.resources.getString(R.string.ps_history_tax_title)
            PaymentType.FINE_PAYMENT.name -> context.resources.getString(R.string.ps_history_fine_title)
            else -> ""
        }

    /** menu filter */
    override fun onMenuItem1() {
        action.value = HistoryFragmentDirections.historyFilterFragmentAction()
    }

    private fun onHistoryClick(data: Any?) {
        data?.let {
            val history = it as History
            paramsRepository.saveData(PaymentStartParams(id = history.id))

            val url =  when(history.type) {
                PaymentType.PAYMENT.name -> context.resources.getString(R.string.ps_tax_payment_done_url)
                PaymentType.TAX_PAYMENT.name -> context.resources.getString(R.string.ps_uid_tax_payment_done_url)
                PaymentType.FINE_PAYMENT.name -> context.resources.getString(R.string.ps_fine_payment_done_url)
                else -> ""
            }

            actionDeepLink.value = NavDeepLinkRequest.Builder
                .fromUri(url.toUri())
                .build()
        }
    }
}