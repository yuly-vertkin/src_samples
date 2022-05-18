package ru.russianpost.payments.base.ui

import android.Manifest

internal const val BASE_URL = "https://opay.test.russianpost.ru/"
internal const val FRAGMENT_PARAMS_NAME = "params"
internal const val CHECK_PDF_NAME = "check.pdf"
internal const val CHECK_MIME_TYPE = "application/pdf"
internal const val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

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
internal const val SUM_FRACTION_NUMBER = 2
internal const val CARD_NUMBER_LENGTH = 19
internal const val CARD_CVC_LENGTH = 3
internal const val CARD_NUMBER_TEMPLATE = "#### #### #### ####"
internal const val CARD_VALID_THRU_LENGTH = 5
internal const val CARD_VALID_THRU_TEMPLATE = "##/##"
internal const val AUTO_DOCUMENTS_TEMPLATE = "## ## ######" // СТС или ВУ автомобиля
internal const val AUTO_FINE_DAYS_BEFORE = 2
internal const val VRC_LENGTH = 12
internal const val DL_LENGTH = 12
internal const val VRC_ADVICE = "STS"
internal const val DL_ADVICE = "VU"

internal const val SIMPLE_DIALOG_REQUEST_KEY = "SIMPLE_DIALOG_REQUEST"
internal const val SIMPLE_DIALOG_RESULT_KEY = "SIMPLE_DIALOG_RESULT"

internal const val INPUT_TYPE_TEXT = "text"
internal const val INPUT_TYPE_NUMBER = "number"
internal const val INPUT_TYPE_NUMBER_DECIMAL = "numberDecimal"
internal const val INPUT_TYPE_NUMBER_PASSWORD = "numberPassword"
