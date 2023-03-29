package ru.russianpost.payments.features.history.ui

import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.MutableLiveData
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.history.domain.HistoryRepository
import ru.russianpost.payments.tools.formatSimpleDate
import java.util.*
import javax.inject.Inject

/**
 * ViewModel фильтра истории платежей
 */
internal class HistoryFilterViewModel @Inject constructor(
    private val repository: HistoryRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    val showDateRangePicker = MutableLiveData<Boolean>()
    private var period: String? = null

    override fun onCreate() {
        super.onCreate()
        showDateRangePicker.value = false

        val historyData = repository.getData()
        period = historyData.period

        with(context.resources) {
            historyData.let {
                addFields(listOf(
                    TextFieldValue(
                        text = getString(R.string.ps_category).uppercase(),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_plastique),
                        textSize = getDimension(R.dimen.ps_text_size_12sp),
                    ),
                    CheckboxFieldValue(
                        id = R.id.ps_filter_tax,
                        title = getString(R.string.ps_filter_tax),
                        selected = MutableLiveData(it.taxFilter),
                        isRightCheckbox = true,
                    ),
                    CheckboxFieldValue(
                        id = R.id.ps_filter_fine,
                        title = getString(R.string.ps_filter_fine),
                        selected = MutableLiveData(it.fineFilter),
                        isRightCheckbox = true,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_period).uppercase(),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_plastique),
                        textSize = getDimension(R.dimen.ps_text_size_12sp),
                    ),
                    ContainerFieldValue(
                        id = R.id.ps_container1,
                        items = listOf(
                            ButtonFieldValue(
                                id = R.id.ps_week,
                                text = MutableLiveData(getString(R.string.ps_week)),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                                checked = MutableLiveData(isCheck(R.id.ps_week)),
                                action = ::onPeriod,
                                data = R.id.ps_week,
                                isExtendedButton = false,
                            ),
                            ButtonFieldValue(
                                id = R.id.ps_month,
                                text = MutableLiveData(getString(R.string.ps_month)),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                                checked = MutableLiveData(isCheck(R.id.ps_month)),
                                action = ::onPeriod,
                                data = R.id.ps_month,
                                isExtendedButton = false,
                            ),
                            ButtonFieldValue(
                                id = R.id.ps_year,
                                text = MutableLiveData(getString(R.string.ps_year)),
                                checked = MutableLiveData(isCheck(R.id.ps_year)),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                                action = ::onPeriod,
                                data = R.id.ps_year,
                                isExtendedButton = false,
                            ),
                        ),
                    ),
                    ButtonFieldValue(
                        id = R.id.ps_custom_period,
                        text = MutableLiveData(if (isCheck(R.id.ps_custom_period)) period else getString(R.string.ps_custom_period)),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        checked = MutableLiveData(isCheck(R.id.ps_custom_period)),
                        action = ::onPeriod,
                        data = R.id.ps_custom_period,
                        isExtendedButton = false,
                    ),
                ))

                addField(
                    ButtonFieldValue(
                        text = MutableLiveData(getString(R.string.ps_show)),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        action = ::onButtonClick,
                    ),
                    isMainFields = false
                )
            }
        }
    }

    private fun isCheck(@IdRes id: Int) : Boolean =
        with(context.resources) {
            when (id) {
                R.id.ps_week -> period == getString(R.string.ps_week)
                R.id.ps_month -> period == getString(R.string.ps_month)
                R.id.ps_year -> period == getString(R.string.ps_year)
                R.id.ps_custom_period -> !period.isNullOrEmpty() && period != getString(R.string.ps_week) &&
                                         period != getString(R.string.ps_month) && period != getString(R.string.ps_year)
                else -> false
            }
        }

    private fun onPeriod(data: Any?) {
        uncheckPeriod()
        val id = data as? Int
        (data as? Int)?.let {
            if (it != R.id.ps_custom_period) {
                get<ButtonFieldValue>(it)?.checked?.value = true
                period = getFieldText(it)
            } else if (!showDateRangePicker.value!!)
                showDateRangePicker.value = true
        }
    }

    private fun uncheckPeriod() {
        period = ""
        get<ButtonFieldValue>(R.id.ps_week)?.checked?.value = false
        get<ButtonFieldValue>(R.id.ps_month)?.checked?.value = false
        get<ButtonFieldValue>(R.id.ps_year)?.checked?.value = false
        get<ButtonFieldValue>(R.id.ps_custom_period)?.checked?.value = false
        setFieldText(R.id.ps_custom_period, context.resources.getString(R.string.ps_custom_period))
    }

    fun onSelectPeriod(periodPair: Pair<Long, Long>) {
        val start = formatSimpleDate(Date(periodPair.first))
        val end = formatSimpleDate(Date(periodPair.second))
        period = "$start - $end"
        setFieldText(R.id.ps_custom_period, period)
        get<ButtonFieldValue>(R.id.ps_custom_period)?.checked?.value = true
        showDateRangePicker.value = false
    }

    fun onCancelPeriod() {
        showDateRangePicker.value = false
    }

    override fun onMenuItem1() {
        get<CheckboxFieldValue>(R.id.ps_filter_tax)?.selected?.value = false
        get<CheckboxFieldValue>(R.id.ps_filter_fine)?.selected?.value = false
        uncheckPeriod()
    }

    private fun onButtonClick(data: Any?) {
        val historyData = repository.getData().copy(
            taxFilter = getCheckboxValue(R.id.ps_filter_tax),
            fineFilter = getCheckboxValue(R.id.ps_filter_fine),
            period = period,
        )
        repository.saveData(historyData)

        actionBack.value = true
    }
}