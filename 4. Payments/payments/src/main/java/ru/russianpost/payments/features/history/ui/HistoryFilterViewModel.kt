package ru.russianpost.payments.features.history.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.features.history.domain.HistoryRepository
import javax.inject.Inject

/**
 * ViewModel фильтра истории платежей
 */
@HiltViewModel
internal class HistoryFilterViewModel @Inject constructor(
    private val repository: HistoryRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        val historyData = repository.getData()

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
                                text = getString(R.string.ps_week),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                            ),
                            ButtonFieldValue(
                                id = R.id.ps_month,
                                text = getString(R.string.ps_month),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                            ),
                            ButtonFieldValue(
                                id = R.id.ps_year,
                                text = getString(R.string.ps_year),
                                horizontalMarginRes = R.dimen.ps_horizontal_margin,
                            ),
                        ),
                    ),
                ))
                btnLabel.value = getString(R.string.ps_close)
            }
        }
    }

//    private fun onShowDetails(data: Any?) {
//        fine?.let {
//            action.value = FineDetailFragmentDirections.toFineRequisitesAction(it)
//        }
//    }

    override fun onButtonClick() {
        val historyData = repository.getData().copy(
            taxFilter = getCheckboxValue(R.id.ps_filter_tax),
            fineFilter = getCheckboxValue(R.id.ps_filter_fine),
        )
        repository.saveData(historyData)

        actionBack.value = true
    }
}