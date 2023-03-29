package ru.russianpost.payments.entities.tax

import ru.russianpost.payments.entities.BaseResponse

/** Реквизиты платежа */
internal data class TaxDetails (
    var id: String? = null,
    //    RecipientInformation
    val recipientBankIdentificationCode: String? = null,
    val accountNumber: String? = null,
    val budgetClassificationCode: String? = null,
    val oktmo: String? = null,
    val recipientIndividualTaxNumber: String? = null,
    val recipientRegistrationReasonCode: String? = null,
    val bankName: String? = null,
    val correspondentAccount: String? = null,
    val recipientName: String? = null,
    //    PayerInformation
    val payerIndividualTaxNumber: String? = null,
    val payerStatus: String? = null,
    val firstName: String? = null,
    val patronymic: String? = null,
    val lastName: String? = null,
    val payerAddress: String? = null,
// not used now
//    val email: String? = null,
//    val servicePayerIdentifier: String? = null,
//    val uniquePaymentIdentifier: String? = null,
    //    PaymentInformation
    val paymentBasis: String? = null,
    val taxPeriod: String? = null,
    val paymentAccrualUniqueIdentifier: String? = null,
    val paymentPurpose: String? = null,
    val sum: Float? = null,
    //    PaymentConfirmation
    val birthDate: String? = null,
    val mobilePhone: String? = null,
    val citizenship: String? = null,
    val passportSeriesAndNumber: String? = null,
    val passportIssuedBy: String? = null,
    val passportWhenIssued: String? = null,
    val organizationName: String? = null,
) : BaseResponse()
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
