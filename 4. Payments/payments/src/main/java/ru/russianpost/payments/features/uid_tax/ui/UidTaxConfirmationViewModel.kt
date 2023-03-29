package ru.russianpost.payments.features.uid_tax.ui

import android.annotation.SuppressLint
import androidx.navigation.NavDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.charges.Charge
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.ConfirmationViewModel
import javax.inject.Inject

/**
 * ViewModel подтверждения платежа
 */
internal class UidTaxConfirmationViewModel @Inject constructor(
    private val repository: ChargesRepository,
    cardRepository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : ConfirmationViewModel(cardRepository, appContextProvider) {
    override val useCommission = false

    override fun onCreate() {
        super.onCreate()

        val uin = repository.getData().uin
        uin?.let {
            processNetworkCall(
                action = { repository.getChargeSaved(it) },
                onSuccess = ::updateData,
                onError = {
                    if (it is ResponseException)
                        showServerErrorDialog(it.errorCode, it.errorTitle, it.errorMessage) { actionBack.value = true }
                    else
                        showServiceUnavailableDialog { actionBack.value = true }
                },
            )
        }
    }

    @SuppressLint("RedundantWith")
    private fun updateData(data: Charge) {
        with(data) {
            setFieldText(R.id.ps_payment_purpose, purpose)
            setFieldText(R.id.ps_payment_sum, makeSum(amountToPay))
            setFieldText(R.id.ps_organization_name, payeeName)
            setFieldText(R.id.ps_tax_id_number_recipient, payeeInn)
            setFieldText(R.id.ps_bank_name, payeeBankName)
            setFieldText(R.id.ps_account_number, payeeCorrespondentBankAccount)
            setFieldText(R.id.ps_bank_id_code, payeeBankBik)

            cardRepository.saveData(cardRepository.getData().copy(
                description = purpose,
                amount = amountToPay,
            ))

            setDataReady()
        }
    }

    override fun getPaymentCardAction(): NavDirections =
        UidTaxConfirmationFragmentDirections.toPaymentCardAction()

    override fun getAddressDialogAction(): NavDirections =
        UidTaxConfirmationFragmentDirections.addressDialogFragmentAction()
}