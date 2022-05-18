package ru.russianpost.payments.entities.tax

/** Подтверждение платежа */
internal data class TaxConfirmation (
    val paymentPurpose: String = "",
    val postalTransferAmount: String = "",
    val fullName: String = "",
    val dateOfBirth: String = "",
    val mobilePhone: String = "",
    val address: String = "",
    val citizenship: String = "",
    val senderID: String = "",
    val seriesAndNumber: String = "",
    val issuedBy: String = "",
    val dateOfIssue: String = "",
    val organizationName: String = "",
    val recipientIndividualTaxNumber: String = "",
    val bankName: String = "",
    val accountNumber: String = "",
    val recipientBankIdentificationCode: String = "",
    val postcode: String = "",
)