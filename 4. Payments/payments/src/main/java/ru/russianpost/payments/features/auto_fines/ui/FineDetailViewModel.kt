package ru.russianpost.payments.features.auto_fines.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.auto_fines.AutoFine
import ru.russianpost.payments.tools.formatServerDate
import javax.inject.Inject

/**
 * ViewModel описания штрафа
 */
@HiltViewModel
internal class FineDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var fine: AutoFine? = null

    override fun onCreateView() {
        super.onCreateView()

        fine = savedStateHandle.get<AutoFine>(FRAGMENT_PARAMS_NAME)

        with(context.resources) {
            fine?.let {
                addFields(listOf(
                    CellFieldValue(
                        id = R.id.ps_violation,
                        title = getString(R.string.ps_violation),
                        subtitle = MutableLiveData(it.purpose),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        id = R.id.ps_fine_date,
                        title = getString(R.string.ps_fine_date),
                        subtitle = MutableLiveData(formatServerDate(it.offenseDate)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        id = R.id.ps_fine_place,
                        title = getString(R.string.ps_fine_place),
                        subtitle = MutableLiveData(it.offensePlace),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        id = R.id.ps_doc_number,
                        title = getString(R.string.ps_charges_uid_document),
                        subtitle = MutableLiveData(it.supplierBillID),
                        isValueCell = true,
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_show_details),
                        endDrawableRes = R.drawable.ic24_navigation_chevron_right,
                        action = ::onShowDetails,
                    ),
                    DividerFieldValue(
                        heightRes = R.dimen.ps_big_divider_height,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_discounted_amount),
                        rightText = makeSum(it.amountToPay),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_commission),
                        rightText = "${it.commission} $rubSign",
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                    ),
                    DividerFieldValue(
                        startMarginRes = R.dimen.ps_horizontal_margin,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_total),
                        rightText = makeSum(it.amountToPay + it.commission),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_20sp),
                    ),
                    CheckboxFieldValue(
                        id = R.id.ps_confirm_not_public_official,
                        title = getString(R.string.ps_confirm_not_public_official),
                        selected = MutableLiveData(true),
                    ),
                ))
                btnLabel.value = getString(R.string.ps_pay_sum, makeSum(it.amountToPay + it.commission))
            }
        }
    }

    private fun onShowDetails(data: Any?) {
        fine?.let {
            action.value = FineDetailFragmentDirections.toFineRequisitesAction(it)
        }
    }

    override fun onButtonClick() {
        fine?.let {
            action.value = FineDetailFragmentDirections.toPaymentCardAction(it.amountToPay + it.commission)
        }
    }
}