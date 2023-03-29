package ru.russianpost.payments.base.ui

import android.Manifest
import ru.russianpost.payments.R

internal const val LOG_TAG = "Payments"

internal const val ERROR_NO_NETWORK = 5000
internal const val POOLING_TIMEOUT = 5000
internal const val NO_ERROR = 0
internal const val ERROR_POOLING = 1020

internal const val METRICS_LOCATION_PREFIX = "Платежи_"
internal const val METRICS_TARGET_SELF = "self"
internal const val METRICS_TARGET_BACK = "назад"
internal const val METRICS_ACTION_OPEN = "открытие_экрана"
//internal const val METRICS_ACTION_CLOSE = "закрытие_экрана"
internal const val METRICS_ACTION_TAP = "тап"
internal const val METRICS_ACTION_PAYMENT_SUCCESS = "успешная оплата"
internal const val METRICS_ACTION_PAYMENT_FAIL = "не успешная оплата"

internal const val FRAGMENT_PARAMS_NAME = "params"
internal const val CHECK_PDF_NAME = "check.pdf"
internal const val CHECK_MIME_TYPE = "application/pdf"
internal const val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

internal const val OLD_PAYMENT_URL = "https://finance.pochta.ru/payments/"
internal const val OLD_PAYMENT_SECTION1 = "Perevody"
internal const val OLD_PAYMENT_SECTION2 = "Kommunalnye_uslugi"
internal const val OLD_PAYMENT_SECTION3 = "mobile"
internal const val OLD_PAYMENT_SECTION4 = "telephone"
internal const val OLD_PAYMENT_SECTION5 = "Internet"
internal const val OLD_PAYMENT_SECTION6 = "Televidenie"
internal const val OLD_PAYMENT_SECTION7 = "Accounts"
internal const val OLD_PAYMENT_SECTION8 = "other"

internal const val ZERO_LENGTH = 0
internal const val INN_LENGTH = 12
internal const val UIN_LENGTH1 = 20
internal const val UIN_LENGTH2 = 25
internal const val BANK_ID_CODE_LENGTH = 9
internal const val ACCOUNT_NUMBER_LENGTH = 20
internal const val ACCOUNT_NUMBER_PREFIX = "03"
internal const val BUDGET_CLASSIFICATION_CODE_LENGTH = 20
internal const val OKTMO_LENGTH = 8
internal const val TAX_ID_NUMBER_RECIPIENT_LENGTH = 10
internal const val REASON_CODE_LENGTH = 9
internal const val YEAR_LENGTH = 4
internal const val TAX_PERIOD_LENGTH = 10
internal const val TAX_PERIOD_PREFIX = "ГД.00."
internal const val TAX_PERIOD_START_YEAR = 2000
internal const val TAX_ID_NUMBER_PAYER_LENGTH = 12
internal const val SUM_LENGTH = 7
internal const val SUM_FRACTION_NUMBER = 2
internal const val CARD_NUMBER_LENGTH = 19
//internal const val CARD_CVC_LENGTH = 3
//internal const val CARD_NUMBER_TEMPLATE = "#### #### #### ####"
internal const val CARD_VALID_THRU_LENGTH = 5
//internal const val CARD_VALID_THRU_TEMPLATE = "##/##"
internal const val AUTO_DOCUMENTS_TEMPLATE = "## ## ######" // СТС или ВУ автомобиля
internal const val AUTO_FINE_DAYS_BEFORE = 2
internal const val VRC_LENGTH = 12
internal const val DL_LENGTH = 12

internal const val VRC_ADVICE = "STS"
internal const val DL_ADVICE = "VU"
internal const val UIN_FINE_ADVICE = "UIN"
internal const val UIN_TAX_ADVICE = "UIN_TAX"
internal const val INN_ADVICE = "INN"
internal const val PDL_ADVICE = "PDL"
internal const val OFFER_ADVICE = "OFERTA"

internal const val UID_TAX_UIN_KEY = "docIdx"
internal const val FINE_UIN_KEY = "ruleId"

internal const val FRAGMENT_REQUEST_KEY = "FRAGMENT_REQUEST"
internal const val FRAGMENT_RESULT_KEY = "FRAGMENT_RESULT"
internal const val SIMPLE_DIALOG_REQUEST_KEY = "SIMPLE_DIALOG_REQUEST"
internal const val SIMPLE_DIALOG_RESULT_KEY = "SIMPLE_DIALOG_RESULT"

internal const val INPUT_TYPE_TEXT = "text"
internal const val INPUT_TYPE_NUMBER = "number"
internal const val INPUT_TYPE_NUMBER_DECIMAL = "numberDecimal"
internal const val INPUT_TYPE_NUMBER_PASSWORD = "numberPassword"

internal const val IME_ACTION_NEXT = "actionNext"
internal const val IME_ACTION_DONE = "actionDone"
internal const val AUTOCOMPLETE_DEFAULT_THRESOLD = 2

internal val topLevelDestinationIds = setOf(R.id.mainFragment, R.id.paymentCardFragment, R.id.taxPaymentDoneFragment,
    R.id.uidTaxPaymentDoneFragment, R.id.finePaymentDoneFragment, R.id.authDialogFragment)