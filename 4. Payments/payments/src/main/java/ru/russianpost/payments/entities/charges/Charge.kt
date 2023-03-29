package ru.russianpost.payments.entities.charges

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import ru.russianpost.payments.entities.BaseResponse

/** Реквизиты начисления
 * https://confluence.tools.russianpost.ru/pages/viewpage.action?pageId=343058149#id-%D0%A8%D1%82%D1%80%D0%B0%D1%84%D1%8B%D0%93%D0%98%D0%91%D0%94%D0%94%D0%BF%D0%BE%D0%B8%D1%81%D0%BA%D0%BF%D0%BE%D0%A1%D0%A2%D0%A1-%D0%9F%D0%BE%D0%BB%D1%8F:
 * */
@Parcelize
data class Charge (
    val payerIdentifier: String, // 1 Номер СТС
    val supplierBillID: String, //7 Номер документа, 27 УИН
    val billDate: String,  //8 Дата документа
    val payerName: String, //9 ФИО
    val payeeInn: String, //10 ИНН
    val payeeKpp: String, // 11 КПП
    val payeeName: String, // 12 Наименование получателя
    val payeeBankBik: String, // 13 БИК
    val payeeCorrespondentBankAccount: String,  // 14 Счет (номер счета)
    //? 15 Банк получателя
    val kbk: String, // 16 КБК
    val oktmo: String,    // 17 ОКТМО
    val totalAmount: Float, // 18 Сумма начисления, 29 Сумма (перевода)
    //? 19 Сообщение о скидке - формируется на клиенте c помощью discount + discountExpiry
    val discount: Float?,    // 20 Сумма штрафа со скидкой
    val discountExpiry: String?,
    //? 21  Статус начисления Не оплачено или оплачено
    val amountToPay: Float, // 22 Сумма к оплате
    val offensePlace: String?, // 23 Место нарушения
    val offenseDate: String?, // 24 Дата и время нарушения
    val legalAct: String?, //25 Тип нарушения
    val purpose: String, // 26 Назначение платежа
    // 27 УИН - см. 7 - supplierBillID
    val payeeBankName: String?,
) : BaseResponse(), Parcelable
