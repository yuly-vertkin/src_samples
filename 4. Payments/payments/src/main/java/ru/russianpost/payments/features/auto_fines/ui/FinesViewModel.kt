package ru.russianpost.payments.features.auto_fines.ui

import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.strikeThrough
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.auto_fines.AutoFine
import ru.russianpost.payments.entities.auto_fines.AutoFinesData
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.tools.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * ViewModel списка штрафов по СТС и ВУ
 */
@HiltViewModel
internal class FinesViewModel @Inject constructor(
    private val repository: AutoFinesRepository,
    private val savedStateHandle: SavedStateHandle,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private lateinit var autoFineData: AutoFinesData
    @ColorInt private var discountColor: Int = 0

    override fun onCreateView() {
        super.onCreateView()

        autoFineData = savedStateHandle.get<AutoFinesData>(FRAGMENT_PARAMS_NAME) ?: repository.getData()
        savedStateHandle.remove<AutoFinesData>(FRAGMENT_PARAMS_NAME)

        discountColor = ContextCompat.getColor(context, R.color.common_jardin)
        isBtnVisible.value = false

        viewModelScope.launch {
            repository.getAutoFines(autoFineData).collect {
                isLoading.value = it is Response.Loading
                when(it) {
                    is Response.Success -> addFines(it.data)
                    is Response.Error ->  {
                        addFines(emptyList())
                        if (!isDocumentsEmpty())
                            showDialog.value = DialogTypes.SERVICE_UNAVAILABLE
                    }
                    else -> {}
                }
            }
        }
    }

    private fun addFines(fines: List<AutoFine>) {
        if(fines.isEmpty()) {
            with(context.resources) {
                addField(
                    TextFieldValue(
                        id = R.id.ps_fines_not_found,
                        text = if (isDocumentsEmpty()) getString(R.string.ps_documents_empty) else getString(R.string.ps_fines_not_found),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    )
                )
            }
        }

        fines.map {
            addFields(listOf(
                AutoFineFieldValue(
                    violation = it.purpose,
                    date = getFineDate(it.offenseDate),
                    sum = getSum(it),
                    details = context.resources.getString(R.string.ps_discount_msg,
                       "${it.discountFixed} $rubSign",
                        formatReverseDate(it.discountExpiry)),
                    data = it,
                    action = ::onFineClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                )
            ))
        }

        with(context.resources) {
            addField(
                CellFieldValue(
                    title = if (isDocumentsEmpty()) getString(R.string.ps_add_documents) else getString(R.string.ps_my_documents),
                    startDrawableRes = if (isDocumentsEmpty()) R.drawable.ic24_action_add else R.drawable.ic24_user_passport,
                    startDrawableColorRes = R.color.common_xenon,
                    action = ::onShowDocuments,
                ),
            )
        }
    }

    private fun isDocumentsEmpty() =
        autoFineData.vehicleRegistrationCertificates.isNullOrEmpty() &&
        autoFineData.driverLicenses.isNullOrEmpty()

    private fun onShowDocuments(data: Any?) {
        action.value = if (isDocumentsEmpty())
                FinesFragmentDirections.toFineAddDocumentDialogAction()
            else
                FinesFragmentDirections.toFineDocumentsAction()
    }

    private fun getFineDate(dateStr: String) : String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
        getDateFromStr(dateStr, inputFormat)?.let {
            return when {
                isToday(it.time) -> getTimeFromDate(dateStr)
                isDaysBefore(it.time, AUTO_FINE_DAYS_BEFORE) -> getDayFromDate(dateStr)
                else -> formatReverseDate(dateStr)
            }
        } ?: return ""
    }

    private fun getSum(fine: AutoFine) =
        if (fine.amountToPay < fine.totalAmount)
            makeDiscountText(makeSum(fine.amountToPay), makeSum(fine.totalAmount))
        else makeSum(fine.totalAmount)

    private fun makeDiscountText(sumPay: String, sumTotal: String) : Spanned {
        return buildSpannedString {
            color(discountColor) {
                append(sumPay)
            }
            append(" ")
            strikeThrough {
                append(sumTotal)
            }
        }
    }

    fun onActionSettings() {
        action.value = FinesFragmentDirections.toFineSettingsAction()
    }

    private fun onFineClick(data: Any?) {
        data?.let {
            val fine = it as AutoFine

            viewModelScope.launch {
                repository.sendFine(fine).collect {
                    isLoading.value = it is Response.Loading
                    when(it) {
                        is Response.Success -> {
                            autoFineData = repository.getData().copy(
                                currentFineId = it.data,
                            )
                            repository.saveData(autoFineData)

                            action.value = FinesFragmentDirections.toFineAction(fine)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}