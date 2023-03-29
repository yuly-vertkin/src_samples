package ru.russianpost.payments.features.uid_tax.ui

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
import ru.russianpost.payments.tools.getDateFromStr
import ru.russianpost.payments.tools.getYearFromDate
import java.text.SimpleDateFormat
import java.util.*

/**
 * ViewModel списка налогов по ИНН
 */
internal class UidTaxesViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    appContextProvider: AppContextProvider,
) : BaseMenuViewModel(appContextProvider) {
    @ColorInt private var discountColor: Int = 0

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<UidTaxesViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): UidTaxesViewModel
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
            inn = chargesData.inn,
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
                    id = R.id.ps_uid_search,
                    text = getString(R.string.ps_uid_tax_searching),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    textSize = getDimension(R.dimen.ps_text_size_16sp),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                )
            )
        }
    }

    private fun addCharges(charges: List<Charge>) {
        setVisibility(R.id.ps_uid_search, false)

        if (charges.isEmpty()) {
            with(context.resources) {
                addField(
                    TextFieldValue(
                        text = getString(R.string.ps_uid_tax_not_found),
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
                    date = getChargeDate(it.billDate),
                    sum = getSum(it),
                    details = "",
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
        repository.getData().inn.isNullOrEmpty()

    private fun onShowDocuments(data: Any?) {
        action.value = if (isDocumentsEmpty())
                UidTaxesFragmentDirections.uidTaxAddDocumentFragmentAction()
            else
                UidTaxesFragmentDirections.uidTaxDocumentsFragmentAction()
    }

    private fun getChargeDate(dateStr: String?) : String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return getDateFromStr(dateStr, inputFormat)?.let {
                    context.getString(R.string.ps_uid_tax_for, getYearFromDate(dateStr))
               } ?: ""
    }

    private fun getSum(fine: Charge) =
        if (fine.amountToPay < fine.totalAmount)
            makeSumMessage(makeSum(fine.amountToPay), makeSum(fine.totalAmount))
        else makeSum(fine.totalAmount)

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
            trim()
        }
    }

    /** menu settings */
    override fun onMenuItem1() {
        action.value = UidTaxesFragmentDirections.uidTaxSettingsFragmentAction()
    }

    private fun onChargeClick(data: Any?) {
        data?.let {
            val fine = it as Charge
            val chargesData = repository.getData().copy(
                uin = fine.supplierBillID,
            )
            repository.saveData(chargesData)

            action.value = UidTaxesFragmentDirections.uidTaxDetailFragmentAction(fine)
        }
    }
}