package ru.russianpost.payments.features.auto_fines.ui

import android.text.Spanned
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.text.strikeThrough
import androidx.lifecycle.SavedStateHandle
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.entities.charges.ChargesData
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.tools.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel списка штрафов по СТС и ВУ
 */
internal class FinesViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    @ColorInt private var discountColor: Int = 0

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FinesViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FinesViewModel
    }

    override fun onCreate() {
        super.onCreate()

        discountColor = ContextCompat.getColor(context, R.color.common_jardin)

        showSearchString()
        // вызывается здесь, чтобы не вызываться повторно в onCreateView при возврате на экран
        makeNetworkCall()
    }

    override fun onCreateView() {
        super.onCreateView()

        var chargesData = repository.getData()
        // вызывается только если мы изменили документы
        if (chargesData.updateDocuments) {
            clearFields()
            showSearchString()
            makeNetworkCall()

            chargesData = chargesData.copy(
                updateDocuments = false
            )
            repository.saveData(chargesData)
        }
    }

    private fun makeNetworkCall() {
        var chargesData = repository.getData()
        if (isDocumentsEmpty()) {
            if (chargesData.updateDocuments) { // пользователь удалил все документы
                addCharges(emptyList())
                return
            } else { // пользователь решил не сохранять введенные документы
                chargesData = savedStateHandle.get<ChargesData>(FRAGMENT_PARAMS_NAME) ?: repository.getData()
            }
        }
// откомментировать если мы не хотим показывать штрафы для несохраненных документов между переходами по экранам
// (уход на др. экран и возврат приведут к потере несохраненного документа)
//        savedStateHandle.remove<ChargesData>(FRAGMENT_PARAMS_NAME)

        // search with only payment fields
        val data = ChargesData(
                vehicleRegistrationCertificates = chargesData.vehicleRegistrationCertificates,
                driverLicenses = chargesData.driverLicenses,
            )
        processNetworkCall(
            action = { repository.getCharges(data) },
            onSuccess = ::addCharges,
            onError = {
                addCharges(emptyList())
                if (it is ResponseException)
                    showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage)
                else if (!isDocumentsEmpty())
                    showServiceUnavailableDialog()
            },
        )
    }

    private fun showSearchString() {
        with(context.resources) {
            addField(
                TextFieldValue(
                    id = R.id.ps_fines_search,
                    text = getString(R.string.ps_fine_search),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    textSize = getDimension(R.dimen.ps_text_size_16sp),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                )
            )
        }
    }

    private fun addCharges(charges: List<Charge>) {
        val chargesData = repository.getData()
        setVisibility(R.id.ps_fines_search, false)

        if (charges.isEmpty()) {
            with(context.resources) {
                addField(
                    TextFieldValue(
                        text = if (isDocumentsEmpty() && chargesData.updateDocuments) getString(R.string.ps_documents_empty)
                               else getString(R.string.ps_fines_not_found),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_text_vertical_margin,
                    )
                )
            }
        }

        charges.map {
            addFields(listOf(
                ChargeFieldValue(
                    violation = it.purpose,
                    date = getChargeDate(it.offenseDate),
                    sum = getSum(it),
                    details = makeDiscountMessage(it),
                    isDetailVisible = it.discount != null,
                    data = it,
                    action = ::onChargeClick,
                ),
                DividerFieldValue(
                    startMarginRes = R.dimen.ps_horizontal_margin,
                )
            ))
        }

        with(context.resources) {
            addFields(listOf(
                CellFieldValue(
                    title = if (isDocumentsEmpty()) getString(R.string.ps_add_documents) else getString(R.string.ps_my_documents),
                    startDrawableRes = if (isDocumentsEmpty()) R.drawable.ic24_action_add else R.drawable.ic24_user_passport,
                    startDrawableColorRes = R.color.grayscale_stone,
                    action = ::onShowDocuments,
                ),
            ))
        }
    }

    private fun isDocumentsEmpty() =
        repository.getData().vehicleRegistrationCertificates.isNullOrEmpty() &&
        repository.getData().driverLicenses.isNullOrEmpty()

    private fun onShowDocuments(data: Any?) {
        action.value = if (isDocumentsEmpty())
                FinesFragmentDirections.toFineAddDocumentDialogAction()
            else
                FinesFragmentDirections.toFineDocumentsAction()
    }

    private fun getChargeDate(dateStr: String?) : String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return getDateFromStr(dateStr, inputFormat)?.let {
            when {
                isToday(it.time) -> getTimeFromDate(dateStr)
                isDaysBefore(it.time, AUTO_FINE_DAYS_BEFORE) -> getDayFromDate(dateStr)
                else -> formatReverseDate(dateStr)
            }
        } ?: ""
    }

    private fun getSum(charge: Charge) =
        if (charge.amountToPay < charge.totalAmount)
            makeSumMessage(makeSum(charge.amountToPay), makeSum(charge.totalAmount))
        else makeSum(charge.totalAmount)

    private fun makeSumMessage(sumPay: String?, sumTotal: String?) : Spanned {
        return buildSpannedString {
            sumPay?.let {
                color(discountColor) {
                    append(sumPay)
                }
            }
            append(" ")
            sumTotal?.let {
                strikeThrough {
                    append(sumTotal)
                }
            }
        }
    }

    private fun makeDiscountMessage(charge: Charge) =
        charge.discount?.let { context.resources.getString(R.string.ps_discount_msg,
            makeSum(charge.discount), formatReverseDate(charge.discountExpiry)) } ?: ""

    /** menu settings */
    override fun onMenuItem1() {
        action.value = FinesFragmentDirections.toFineSettingsAction()
    }

    private fun onChargeClick(data: Any?) {
        data?.let {
            val charge = it as Charge
            val chargesData = repository.getData().copy(
                uin = charge.supplierBillID,
            )
            repository.saveData(chargesData)

            action.value = FinesFragmentDirections.toFineAction(charge)
        }
    }
}