package ru.russianpost.payments.features.auto_fines.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavDirections
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.payments.AutoFinesNavGraphDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.base.di.AssistedSavedStateViewModelFactory
import ru.russianpost.payments.base.domain.BaseInputFieldFormatter
import ru.russianpost.payments.base.domain.EmailValidator
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.data.network.sendAnalyticsEvent
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.BaseResponse
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.entities.charges.ChargesData
import ru.russianpost.payments.entities.payment_card.CardDetail
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.BaseCardWorkViewModel
import ru.russianpost.payments.tools.SnackbarParams
import ru.russianpost.payments.tools.formatServerDate

/**
 * ViewModel описания штрафа
 */
internal class FineDetailViewModel @AssistedInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val repository: ChargesRepository,
    cardRepository: PaymentCardRepository,
    paramsRepository: PaymentStartParamsRepository,
    appContextProvider: AppContextProvider,
) : BaseCardWorkViewModel(cardRepository, paramsRepository, appContextProvider) {

    @AssistedFactory
    interface Factory : AssistedSavedStateViewModelFactory<FineDetailViewModel> {
        override fun create(savedStateHandle: SavedStateHandle): FineDetailViewModel
    }

    override fun onCreate() {
        super.onCreate()

        with(context.resources) {
            addField(
                TextFieldValue(
                    id = R.id.ps_uid_search,
                    text = getString(R.string.ps_uid_search),
                    textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                    textSize = getDimension(R.dimen.ps_text_size_16sp),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    verticalMarginRes = R.dimen.ps_text_vertical_margin,
                )
            )
        }

        val charge = savedStateHandle.get<Charge>(FRAGMENT_PARAMS_NAME)

        if (charge == null) {
            val chargeData = repository.getData()
            // search with only payment fields
            val data = ChargesData(
                uin = chargeData.uin,
            )
            processNetworkCall(
                action = { repository.getCharges(data) },
                onSuccess = ::showFineDetails,
                onError = ::showError,
            )
        } else {
            showFineDetails(listOf(charge))
        }
    }

    private fun showFineDetails(charges: List<Charge>) {
        setVisibility(R.id.ps_uid_search, false)

        val charge = charges.firstOrNull()

        with(context.resources) {
            charge?.let {
                addFields(listOf(
                    DividerFieldValue(
                        heightRes = R.dimen.ps_zero_height,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_auth_need),
                        backgroundRes = R.drawable.ps_common_choufleur_background,
                        startDrawableRes = R.drawable.ic24_sign_warning_circle,
                        startDrawableColorRes = R.color.common_sky,
                        verticalMarginRes = R.dimen.ps_horizontal_margin,
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_violation),
                        subtitle = MutableLiveData(it.legalAct),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_fine_date),
                        subtitle = MutableLiveData(formatServerDate(it.offenseDate)),
                        isValueCell = true,
                    ),
                    CellFieldValue(
                        title = getString(R.string.ps_fine_place),
                        subtitle = MutableLiveData(it.offensePlace),
                        isValueCell = true,
                    ),
                    CellFieldValue(
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
                        endDrawableColorRes = R.color.grayscale_stone,
                        action = ::onShowRequisites,
                        data = it,
                    ),
                    DividerFieldValue(
                        heightRes = R.dimen.ps_big_divider_height,
                    ),
                    TextFieldValue(
                        text = getString(R.string.ps_amount),
                        rightText = MutableLiveData(makeSum(it.amountToPay)),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                    ),
                    InputFieldValue(
                        id = R.id.ps_email,
                        title = getString(R.string.ps_email),
                        text = MutableLiveData(cardRepository.getData().email),
                        hint = getString(R.string.ps_email_hint),
                        inputType = INPUT_TYPE_TEXT,
                        imeOptions = IME_ACTION_DONE,
                        formatter = BaseInputFieldFormatter(),
                        validator = EmailValidator(),
                    ),
                ))
            }
        }
    }

    private fun showError(e: Throwable) {
        setVisibility(R.id.ps_uid_search, false)

        if (e is ResponseException)
            showServerErrorDialog(e.errorCode, e.errorTitle, e.errorMessage, ::onError)
        else {
            with(context.resources) {
                addField(
                    EmptyFieldValue(
                        title = getString(R.string.ps_error_service_unavailable),
                        subtitle = MutableLiveData(getString(R.string.ps_error_try_later)),
                        drawableRes = R.drawable.ic24_sign_error,
                        drawableColorRes = R.color.grayscale_stone,
                        verticalMarginRes = R.dimen.ps_huge_margin,
                        actionText = getString(R.string.ps_proceed),
                        action = ::onError,
                    )
                )
                clearFields(isMainFields = false)
            }
        }
    }

    private fun onShowRequisites(data: Any?) {
        val buttonTitle = context.getString(R.string.ps_show_details)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        data?.let {
            action.value = FineDetailFragmentDirections.toFineRequisitesAction(it as Charge)
        }
    }

    override fun showAdvice(data: Any?) {
        val buttonTitle = makeOfferText(R.string.ps_offer_agree, R.string.ps_offer).toString()
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        action.value = AutoFinesNavGraphDirections.toAdviceAction((data as? String).orEmpty())
    }

    private fun onError(data: Any? = null) {
        actionBack.value = true
    }

    override fun geSelectCardAction(param: Array<CardDetail>) : NavDirections =
        FineDetailFragmentDirections.selectCardAction(param)

    override fun onButtonClick(data: Any?) {
        if (!validateAll(context.resources)) {
            showSnackbar.value = SnackbarParams(R.string.ps_error_in_form, style = Snackbar.Style.ERROR)
            return
        }

        val buttonTitle = context.getString(R.string.ps_pay_simple)
        sendAnalyticsEvent(title.value, buttonTitle, METRICS_ACTION_TAP)

        // Проверка возможности оплаты начисления, например при превышении 15к р.
        repository.getData().uin?.let {
            processNetworkCall(
                action = { cardRepository.paymentAvailabilityCheck(it) },
                onSuccess = ::paymentAvailable,
                onError = {
                    if (it is ResponseException)
                        showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage)
                    else
                        showServiceUnavailableDialog()
                },
            )
        }
    }

    private fun paymentAvailable(data: BaseResponse) {
        val email = getFieldText(R.id.ps_email)
        cardRepository.saveData(cardRepository.getData().copy(
            uin = repository.getData().uin,
            email = email,
        ))
        action.value = FineDetailFragmentDirections.toAuthDialogAction()
    }
}