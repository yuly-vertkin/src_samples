package ru.russianpost.payments.features.payment_card.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.payment_card.Commission
import ru.russianpost.payments.entities.payment_card.EsiaData
import ru.russianpost.payments.entities.payment_card.EsiaParams
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository

/**
 * ViewModel подтверждения платежа
 */
internal abstract class ConfirmationViewModel(
    protected val cardRepository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    protected open val useCommission = true

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addFields(listOf(
                DividerFieldValue(
                    heightRes = R.dimen.ps_zero_height,
                ),
                TextFieldValue(
                    text = getString(R.string.ps_confirmation_title),
                    textSize = getDimension(R.dimen.ps_text_size_20sp),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_payment_confirmation),
                    backgroundRes = R.drawable.ps_common_choufleur_background,
                    startDrawableRes = R.drawable.ic24_sign_warning_circle,
                    startDrawableColorRes = R.color.common_sky,
                    verticalMarginRes = R.dimen.ps_horizontal_margin,
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_sum,
                    title = getString(R.string.ps_amount),
                    isValueCell = true,
                ),
            ))
            if (useCommission)
                addFields(listOf(
                    CellFieldValue(
                        id = R.id.ps_commission,
                        title = getString(R.string.ps_commission),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        id = R.id.ps_total,
                        title = getString(R.string.ps_total),
                        isValueCell = true,
                    ),
                ))
            addFields(listOf(
                CellFieldValue(
                    id = R.id.ps_sender_full_name,
                    title = getString(R.string.ps_sender_full_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_sender_address,
                    title = getString(R.string.ps_sender_address),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_zip_code,
                    title = getString(R.string.ps_zip_code),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_mobile_phone,
                    title = getString(R.string.ps_mobile_phone),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_sender_id,
                    title = getString(R.string.ps_sender_id),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_series_and_number,
                    title = getString(R.string.ps_series_and_number),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_when_issued,
                    title = getString(R.string.ps_when_issued),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_issued_by,
                    title = getString(R.string.ps_issued_by),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_citizenship,
                    title = getString(R.string.ps_citizenship),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_birth_date,
                    title = getString(R.string.ps_birth_date),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_organization_name,
                    title = getString(R.string.ps_organization_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_tax_id_number_recipient,
                    title = getString(R.string.ps_tax_id_number_recipient),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_bank_name,
                    title = getString(R.string.ps_bank_name),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_account_number,
                    title = getString(R.string.ps_payment_account),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_bank_id_code,
                    title = getString(R.string.ps_recipient_bank_id_code),
                    isValueCell = true,
                ),
                CellFieldValue(
                    id = R.id.ps_payment_purpose,
                    title = getString(R.string.ps_payment_purpose),
                    isValueCell = true,
                ),
                CheckboxFieldValue(
                    id = R.id.ps_confirm_not_public_official,
                    title = getString(R.string.ps_confirm_not_public_official),
                    selected = MutableLiveData(true),
                    action = ::confirmNotPublicOfficialClick,
                ),
                CellFieldValue(
                    title = getString(R.string.ps_what_public_official),
                    titleColorRes = R.color.common_xenon,
                    action = ::showAdvice,
                    data = PDL_ADVICE,
                ),
            ))

            addField(
                ButtonFieldValue(
                    id = R.id.ps_confirm_button,
                    text = MutableLiveData(getString(R.string.ps_confirm)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    enabled = MutableLiveData(false),
                    action = ::onButtonClick,
                ),
                isMainFields = false
            )
        }

        val cardData = cardRepository.getData()
        val esiaParams = EsiaParams(
            uin = cardData.uin,
            email = cardData.email,
            accessToken = cardData.authResult?.accessToken,
            idToken = cardData.authResult?.idToken,
        )
        processNetworkCall(
            action = { cardRepository.getEsiaData(esiaParams) },
            onSuccess = ::updateEsiaData,
            onError = {
                if (it is ResponseException)
                    showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage) { actionBack.value = true }
                else
                    showServiceUnavailableDialog { actionBack.value = true }
            },
            callName = CONFIRMATION_CALL_NAME,
        )
    }

    private fun updateEsiaData(data: EsiaData) {
        with(data) {
            setFieldText(R.id.ps_sender_full_name, "$firstName $middleName $lastName")
            setFieldText(R.id.ps_birth_date, birthDate)
            setFieldText(R.id.ps_mobile_phone, mobilephone)
            setFieldText(R.id.ps_sender_address, addressRegStr)
            setFieldText(R.id.ps_citizenship, "РФ")
            setFieldText(R.id.ps_sender_id, "Паспорт РФ")
            setFieldText(R.id.ps_series_and_number, "$passportSeries $passportNumber")
            setFieldText(R.id.ps_issued_by, passportIssuedBy)
            setFieldText(R.id.ps_when_issued, passportIssueDate)
            setFieldText(R.id.ps_zip_code, addressRegZipCode)
        }
        esiaReady()
    }

    private fun esiaReady() {
        if (useCommission) {
            val uin = cardRepository.getData().uin
            uin?.let {
                processNetworkCall(
                    action = { cardRepository.getCommission(it) },
                    onSuccess = ::updateCommission,
                    onError = {
                        if (it is ResponseException)
                            showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage) { actionBack.value = true }
                        else
                            showServiceUnavailableDialog { actionBack.value = true }
                    },
                    callName = ESIA_READY_CALL_NAME,
                )
            }
        } else
            setDataReady(CONFIRMATION_CALL_NAME)
    }

    private fun updateCommission(data: Commission) {
        with(data) {
            setFieldText(R.id.ps_payment_sum, makeSum(transferSum))
            setFieldText(R.id.ps_commission, makeSum(transferFee))
            setFieldText(R.id.ps_total, makeSum(totalSum))

            cardRepository.saveData(cardRepository.getData().copy(
                amount = totalSum,
            ))
        }

        setDataReady(ESIA_READY_CALL_NAME)
    }

    protected fun setDataReady(callName: String = DEFAULT_CALL_NAME) {
        if (!isAnotherActiveNetworkCall(callName)) {
            enableConfirmButton(true)

            if (getFieldText(R.id.ps_sender_address).isEmpty())
                action.value = getAddressDialogAction()
        }
    }

    override fun onFragmentResult(result: Bundle) {
        val addressRegStr = result.getString(FRAGMENT_RESULT_KEY)
        if (!addressRegStr.isNullOrEmpty()) {
            setFieldText(R.id.ps_sender_address, addressRegStr)
            enableConfirmButton(false)

            val cardData = cardRepository.getData()
            val esiaParams = EsiaParams(
                address = addressRegStr,
                uin = cardData.uin,
                email = cardData.email,
                accessToken = cardData.authResult?.accessToken,
                idToken = cardData.authResult?.idToken,
            )
            processNetworkCall(
                action = { cardRepository.getEsiaData(esiaParams) },
                onSuccess = { enableConfirmButton(true) },
                onError = {
                    if (it is ResponseException)
                        showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage) { actionBack.value = true }
                    else
                        showServiceUnavailableDialog { actionBack.value = true }
                },
                callName = CONFIRMATION_CALL_NAME,
            )
        }
    }

    private fun confirmNotPublicOfficialClick(data: Any?) {
        val buttonTitle = context.getString(R.string.ps_confirm_not_public_official)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)
    }

    private fun showAdvice(data: Any?) {
        val buttonTitle = context.getString(R.string.ps_what_public_official)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        action.value = AutoFinesNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    private fun onButtonClick(data: Any?) {
        if (getFieldText(R.id.ps_sender_address).isEmpty()) {
            action.value = getAddressDialogAction()
        } else if (!getCheckboxValue(R.id.ps_confirm_not_public_official)) {
            showPublicOfficialDialog()
        } else {
            val buttonTitle = context.getString(R.string.ps_confirm)
            sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

            action.value = getPaymentCardAction()
        }
    }

    private fun enableConfirmButton(enable: Boolean) {
        get<ButtonFieldValue>(R.id.ps_confirm_button, false)?.enabled?.value = enable
    }

    protected abstract fun getPaymentCardAction() : NavDirections

    protected abstract fun getAddressDialogAction() : NavDirections

    companion object {
        const val CONFIRMATION_CALL_NAME = "ConfirmCall"
        const val ESIA_READY_CALL_NAME = "EsiaReadyCall"
    }
}