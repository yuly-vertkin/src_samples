package ru.russianpost.payments.entities.auto_fines

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/** Реквизиты штрафа */
@Parcelize
data class AutoFine (
    val payerIdentifier: String = "", // 1 Номер СТС
    val supplierBillID: String = "", //7 Номер документа, 27 УИН
    val billDate: String = "",  //8 Дата документа
    val payerName: String = "", //9 ФИО
    val payeeInn: String = "", //10 ИНН
    val payeeKpp: String = "", // 11 КПП
    val payeeName: String = "", // 12 Наименование получателя
    val payeeBankBik: String = "", // 13 БИК
    val payeeCorrespondentBankAccount: String = "",  // 14 Счет (номер счета)
    //? 15 Банк получателя
    val kbk: String = "", // 16 КБК
    val oktmo: String = "",    // 17 ОКТМО
    val totalAmount: Float = 0f, // 18 Сумма начисления, 29 Сумма (перевода)
    //? 19 Сообщение о скидке - формируется на клиенте
    //? 20 Сумма штрафа со скидкой - формируется на клиенте
    //? 21  Статус начисления
    val amountToPay: Float = 0f, // 22 Сумма к оплате
    val offensePlace: String = "", // 23 Место нарушения
    val offenseDate: String = "", // 24 Дата и время нарушения
    val legalAct: String = "", //25 Тип нарушения
    val purpose: String = "", // 26 Назначение платежа
    // 27 УИН - см. 7 - supplierBillID
    //? 28 УИП
    val discount: Float = 0f,
    val discountFixed: Int = 0,
    val discountExpiry: String = "",

    // TODO: добавить!
    val commission: Int = 0,
    val bankName: String = "",
    val paymentUid: String = "",
    val address: String = "",
    val postCode: String = "",
    val mobilePhone: String = "",
    val senderId: String = "",
    val seriesAndNumber: String = "",
    val whenIssued: String = "",
    val issuedBy: String = "",
    val citizenship: String = "",
    val birthDate: String = "",
) : Parcelable
/*
@Parcelize
data class AutoFine (
    val payerIdentifier: String = "18810577211217826415", // 1 Номер СТС
    val supplierBillID: String = "18810577211217826415", //7 Номер документа, 27 УИН
    val billDate: String = "2022-02-20T14:06:30.313+03:00",  //8 Дата документа
    val payerName: String = "Тестовый плательщик", //9 ФИО
    val payeeInn: String = "7705401341", //10 ИНН
    val payeeKpp: String = "770542151", // 11 КПП
    val payeeName: String = "ФГБУ «ФКП Росреестра» по г Москва", // 12 Наименование получателя
    val payeeBankBik: String = "024501901", // 13 БИК
    val payeeCorrespondentBankAccount: String = "40102810045370000002",  // 14 Счет (номер счета)
    //? 15 Банк получателя
    val kbk: String = "32111301031016000130", // 16 КБК
    val oktmo: String = "45348000",    // 17 ОКТМО
    val totalAmount: Float = 500f, // 18 Сумма начисления, 29 Сумма (перевода)
    //? 19 Сообщение о скидке - формируется на клиенте
    //? 20 Сумма штрафа со скидкой - формируется на клиенте
    //? 21  Статус начисления
    val amountToPay: Float = 250f, // 22 Сумма к оплате
    val offensePlace: String = "г. Москва, Стандартная ул. д. 21", // 23 Место нарушения
    val offenseDate: String = "2022-02-20T14:06:30.313+03:00", // 24 Дата и время нарушения
    val legalAct: String = "12.15 КоАп РФ", //25 Тип нарушения
    val purpose: String = "Превышение скорости от 20 до 40 км./ч.", // 26 Назначение платежа
    // 27 УИН - см. 7 - supplierBillID
    //? 28 УИП
    val discount: Float = 0f,
    val discountFixed: Int = 38,
    val discountExpiry: String = "2022-02-20T14:06:30.313+03:00",

    // TODO: добавить!
    val commission: Int = 1,
    val bankName: String = "ГУ Банка России по ЦФО по г.Москве",
    val paymentUid: String = "10445252250079782712202102867809",
    val address: String = "г.Москва, Варшавское шоссе, 37",
    val postCode: String = "128096",
    val mobilePhone: String = "+7 926 344 44 44",
    val senderId: String = "Паспорт РФ",
    val seriesAndNumber: String = "9888 567778",
    val whenIssued: String = "19.08.2014",
    val issuedBy: String = "Административным округом",
    val citizenship: String = "РФ",
    val birthDate: String = "15.05.2000",
) : Parcelable
*/

/*
{
    "amountToPay": 2500, // 20 Сумма штрафа со скидкой
    "acknowledgmentStatus": "1",
    "supplierBillID": "32117072411021588933",
    "billDate": "2020-10-31T14:06:30.313+03:00",
    "totalAmount": 5000, // 18 Сумма начисления
    "purpose": "Оплата штрафа за пересечение двойной сплошной", //25 Тип нарушения
    "kbk": "32111301031016000130", // 16 КБК
    "oktmo": "45348000",    // 17 ОКТМО
    "payeeName": "ФГБУ «ФКП Росреестра» по г Москва", // 12 Наименование получателя
    "payeeInn": "7705401341", //10 ИНН
    "payeeKpp": "770542151", // 11 КПП
    "payeeAccountNumber": "03100643000000019500", // 14 Счет
    "payeeBankBik": "024501901", // 13 БИК
    "payeeCorrespondentBankAccount": "40102810045370000002",
    "payerIdentifier": "1240000000007844478122",
    "payerName": "Тестовый плательщик", //9 ФИО
    "budgetIndexStatus": "01",
    "budgetIndexPaytReason": "0",
    "budgetIndexTaxPeriod": "0",
    "budgetIndexTaxDocNumber": "0", //7 Номер документа
    "budgetIndexTaxDocDate": "0", //8 Дата документа
    "changeStatusInfoMeaning": "1",
    "offenseDate": "2021-10-30T14:06:30.313+03:00", // 24 Дата и время нарушения
    "offensePlace": "г. Москва, Стандартная ул д. 21, корп.1, ", // 23 Место нарушения
    "legalAct": "12.15 КоАП РФ",
    "digitalLink": 0,
    "departmentName": 0,
    "discountFixed": 38,
    "expiry": "2022-03-21"
},
*/
