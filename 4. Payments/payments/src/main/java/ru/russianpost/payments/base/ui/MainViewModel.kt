package ru.russianpost.payments.base.ui

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.MainNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.entities.AppContextProvider
import javax.inject.Inject

/**
 * ViewModel главного экрана модуля Платежи
 */
@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        paramsRepository.clearData()

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    id = R.id.ps_tax_details,
                    title = getString(R.string.ps_tax_details),
                    startDrawableRes = R.drawable.ic24_finance_rouble,
                    action = ::onTaxDetailsClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
                CellFieldValue(
                    id = R.id.ps_auto_fines,
                    title = getString(R.string.ps_auto_fines),
                    startDrawableRes = R.drawable.ic24_map_car_outline,
                    action = ::onAutoFinesClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                ),
            ))
        }
        isBtnVisible.value = false
    }

    private fun onTaxDetailsClick(data: Any?) {
        action.value = MainNavGraphDirections.taxFragmentAction()
// temp
/*
        val intent = Intent()
        intent.setClassName("com.octopod.russianpost.client.android", "com.octopod.russianpost.client.android.ui.tracking.registeredmail.RegisteredMailLoginActivity")
        ContextCompat.startActivity(view.context, intent, null)
*/
    }

    private fun onAutoFinesClick(data: Any?) {
        action.value = MainNavGraphDirections.autoFinesFragmentAction()
    }

    fun onActionHistory() {
        action.value = MainFragmentDirections.historyFragmentAction()
    }
}