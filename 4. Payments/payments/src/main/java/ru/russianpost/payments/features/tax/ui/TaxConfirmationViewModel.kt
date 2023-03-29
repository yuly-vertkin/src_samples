package ru.russianpost.payments.features.tax.ui

import androidx.navigation.NavDirections
import ru.russianpost.payments.R
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.tax.TaxConfirmation
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.payment_card.ui.ConfirmationViewModel
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository
import javax.inject.Inject

/**
 * ViewModel подтверждения платежа
 */
internal class TaxConfirmationViewModel @Inject constructor(
    private val repository: TaxDetailsRepository,
    cardRepository: PaymentCardRepository,
    appContextProvider: AppContextProvider,
) : ConfirmationViewModel(cardRepository, appContextProvider) {

    override fun onCreate() {
        super.onCreate()

        val id = repository.getData().id
        id?.let {
            processNetworkCall(
                // TODO: пока не работает
                action = { repository.getTaxConfirmation(/*it*/"8CpT3-202202012019") },
                onSuccess = ::updateData,
                onError = { showServiceUnavailableDialog { actionBack.value = true } },
            )
        }
    }

    private fun updateData(data: TaxConfirmation) {
        with(data) {
            setFieldText(R.id.ps_payment_purpose, paymentPurpose)
            setFieldText(R.id.ps_payment_sum, postalTransferAmount)
            setFieldText(R.id.ps_tax_id_number_recipient, recipientIndividualTaxNumber)
            setFieldText(R.id.ps_bank_name, bankName)
            setFieldText(R.id.ps_account_number, accountNumber)
            setFieldText(R.id.ps_bank_id_code, recipientBankIdentificationCode)

            cardRepository.saveData(cardRepository.getData().copy(
                description = paymentPurpose,
                amount = 0f /*postalTransferAmount*/,
            ))
        }
    }

    override fun getPaymentCardAction(): NavDirections =
        TaxConfirmationFragmentDirections.toPaymentCardAction()

    override fun getAddressDialogAction(): NavDirections =
        TaxConfirmationFragmentDirections.addressDialogFragmentAction()
}