package ru.russianpost.payments.features.history.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.MainNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.PaymentStartParams
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.history.History
import ru.russianpost.payments.entities.history.PaymentType
import ru.russianpost.payments.features.history.domain.HistoryRepository
import ru.russianpost.payments.tools.formatReverseDate
import ru.russianpost.payments.tools.getMonthFromDate
import javax.inject.Inject

/**
 * ViewModel истории платежей
 */
@HiltViewModel
internal class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository,
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        isBtnVisible.value = false

        viewModelScope.launch {
            repository.getHistory().collect {
                isLoading.value = it is Response.Loading
                when(it) {
                    is Response.Success -> addHistory(it.data)
                    is Response.Error -> showDialog.value = DialogTypes.SERVICE_UNAVAILABLE
                    else -> {}
                }
            }
        }
    }

    private fun addHistory(history: List<History>) {
        if(history.isEmpty()) {
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
        history.map {
            val currentDay = getMonthFromDate(it.date)
            if(currentDay != lastDate) {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = if(lastDate != "") R.dimen.ps_big_divider_height else R.dimen.ps_zero_height,
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
                    title = it.title,
                    date = formatReverseDate(it.date),
                    sum = makeSum(it.sum),
                    details = it.desc,
                    data = it,
                    action = ::onHistoryClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                )
            ))
        }
    }

    fun onActionFilter() {
        action.value = HistoryFragmentDirections.historyFilterFragmentAction()
    }

    private fun onHistoryClick(data: Any?) {
        data?.let {
            val history = it as History
            paramsRepository.saveData(PaymentStartParams(id = history.id))
            when(history.type) {
                PaymentType.Tax.name -> action.value = MainNavGraphDirections.taxFragmentAction()
                PaymentType.AutoFine.name -> action.value = MainNavGraphDirections.autoFinesFragmentAction()
            }
        }
    }
}