package ru.russianpost.payments.entities.tax

/** Реквизиты платежа */
internal data class TaxDetails (
    var id: String = "",
    //    RecipientInformation
    val recipientBankIdentificationCode: String = "",
    val accountNumber: String = "",
    val budgetClassificationCode: String = "",
    val oktmo: String = "",
    val recipientIndividualTaxNumber: String = "",
    val recipientRegistrationReasonCode: String = "",
    val bankName: String = "",
    val correspondentAccount: String = "",
    val recipientName: String = "",
    //    PayerInformation
    val payerIndividualTaxNumber: String = "",
    val payerStatus: String = "",
    val firstName: String = "",
    val patronymic: String = "",
    val lastName: String = "",
    val payerAddress: String = "",
// not used now
//    val email: String = "",
//    val servicePayerIdentifier: String = "0",
//    val uniquePaymentIdentifier: String = "0",
    val notPublicOfficial: Boolean = true,
    //    PaymentInformation
    val paymentBasis: String = "",
    val taxPeriod: String = "",
    val paymentAccrualUniqueIdentifier: String = "",
    val paymentPurpose: String = "",
    val sum: Float = 0f,
    //    PaymentConfirmation
    val birthDate: String = "",
    val mobilePhone: String = "",
    val citizenship: String = "",
    val passportSeriesAndNumber: String = "",
    val passportIssuedBy: String = "",
    val passportWhenIssued: String = "",
    val organizationName: String = "",
)
/*
internal data class TaxDetails (
    var id: String = "",
    //    RecipientInformation
    val recipientBankIdentificationCode: String = "02450190",
    val accountNumber: String = "03100643000000017300",
    val budgetClassificationCode: String = "18210601010031000110",
    val oktmo: String = "45394000",
    val recipientIndividualTaxNumber: String = "7721049904",
    val recipientRegistrationReasonCode: String = "772101001",
    val bankName: String = "",//"ГУ Банка России по ЦФО",
    val correspondentAccount: String = "",//"40102810545370000003",
    val recipientName: String = "",//"Управление Федерального Казначейства по г. Москве",
    //    PayerInformation
    val payerIndividualTaxNumber: String = "505399317953",
    val payerStatus: String = "",
    val firstName: String = "Иван",
    val patronymic: String = "Иванович",
    val lastName: String = "Иванов",
    val payerAddress: String = "г.Москва, Варшавское шоссе, 37",
    val email: String = "test@mail.ru",
    val servicePayerIdentifier: String = "0",
    val uniquePaymentIdentifier: String = "0",
    val notPublicOfficial: Boolean = true,
    //    PaymentInformation
    val paymentBasis: String = "",
    val taxPeriod: String = "ГД.00.2020",
    val paymentAccrualUniqueIdentifier: String = "18207721210064679965",
    val paymentPurpose: String = "Оплата налога на имущество физических лиц",
    val sum: Float = 123.456f,
    //    PaymentConfirmation
    val birthDate: String = "12.12.1963",
    val mobilePhone: String = "+7 926 444 44 44",
    val citizenship: String = "РФ",
    val passportSeriesAndNumber: String = "5665 456788",
    val passportIssuedBy: String = "Отделом ФМС г. Москвы Россия",
    val passportWhenIssued: String = "13.12.1986",
    val organizationName: String = "УФК по г. Москве (ИФНС России № 43 по г. Москве)",
)
*/
