package ru.russianpost.payments.base.domain

import android.annotation.SuppressLint
import android.content.res.Resources
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.tools.toFloatOrDefault
import ru.russianpost.payments.tools.toIntOrDefault
import java.util.*

internal open class BaseInputFieldValidator(
    protected open val number: Int = 0
) {
    var isValidate = false

    open fun validate(resources: Resources, text: String?, force: Boolean = false) : String {
        return when {
            !needValidate(force) -> ""
            text.isNullOrEmpty() -> resources.getString(R.string.ps_error_field_empty)
            number != 0 && text.orEmpty().length != number -> resources.getQuantityString(R.plurals.ps_error_field_length, number, number)
            else -> ""
        }
    }

    protected fun needValidate(force: Boolean) = isValidate || force
}

internal class NumbersValidator(
    private val numbers: List<Int>
) : BaseInputFieldValidator() {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        return when {
            !needValidate(force) -> ""
            text.isNullOrEmpty() && !numbers.contains(ZERO_LENGTH) -> resources.getString(R.string.ps_error_field_empty)
            !numbers.contains(text.orEmpty().length) -> resources.getString(R.string.ps_error_field_lengths)
            else -> ""
        }
    }
}

internal class EmptyFieldValidator : BaseInputFieldValidator() {
    override fun validate(resources: Resources, text: String?, force: Boolean): String {
        return ""
    }
}

internal open class NumberOrEmptyFieldValidator(
    override val number: Int
) : BaseInputFieldValidator() {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val textLen = text.orEmpty().length
        return when {
            !needValidate(force) -> ""
            number != 0 && textLen != 0 && textLen != number -> resources.getQuantityString(R.plurals.ps_error_field_length, number, number)
            else -> ""
        }
    }
}

internal open class PrefixValidator(
    private val prefix: String,
    number: Int = 0
) : BaseInputFieldValidator(number) {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            !text.orEmpty().startsWith(prefix) -> resources.getString(R.string.ps_error_prefix, prefix)
            else -> ""
        }
    }
}

internal class TaxPeriodFieldValidator : PrefixValidator(TAX_PERIOD_PREFIX, TAX_PERIOD_LENGTH) {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        val year = if (needValidate(force) && error.isEmpty())
                        text.orEmpty().substring(TAX_PERIOD_PREFIX.length).toIntOrDefault(0)
                   else 0
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            year < TAX_PERIOD_START_YEAR || year > currentYear -> resources.getString(R.string.ps_error_year_tax_period)
            else -> ""
        }
    }
}

internal class YearFieldValidator : BaseInputFieldValidator(YEAR_LENGTH) {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        val year = text.orEmpty().toIntOrDefault(0)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            year < TAX_PERIOD_START_YEAR || year > currentYear -> resources.getString(R.string.ps_error_year_tax_period)
            else -> ""
        }
    }
}

internal class NonZeroNumberValidator(
    number: Int = 0
) : BaseInputFieldValidator(number) {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        val num = if (needValidate(force) && error.isEmpty())
            text.orEmpty().toFloatOrDefault(0f)
        else 0f

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            num == 0f -> resources.getString(R.string.ps_error_non_zero)
            else -> ""
        }
    }
}

internal open class EmailValidator : BaseInputFieldValidator() {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            !text.orEmpty().contains('@') || !text.orEmpty().contains('.')  ||
            text.orEmpty().startsWith("@") || text.orEmpty().endsWith(".") -> resources.getString(R.string.ps_error_invalid_email)
            else -> ""
        }
    }
}

@SuppressLint("unused")
internal class CardValidThruValidator : BaseInputFieldValidator(CARD_VALID_THRU_LENGTH) {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        val date = text.orEmpty().split("/")
        val month = date[0].toIntOrDefault(0)
        val year = if (date.size > 1) date[1].toIntOrDefault(0) else 0
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            month < 1 || month > 12 || year < currentYear -> resources.getString(R.string.ps_error_invalid_date)
            else -> ""
        }
    }
}

/**
 * Алгоритм Лу́на — алгоритм вычисления контрольной цифры номера пластиковой карты в соответствии со стандартом ISO/IEC 7812
 */
@SuppressLint("unused")
internal class LuhnCardNumberValidator : BaseInputFieldValidator(CARD_NUMBER_LENGTH)  {

    override fun validate(resources: Resources, text: String?, force: Boolean) : String {
        val error = super.validate(resources, text, force)

        return when {
            !needValidate(force) -> ""
            error.isNotEmpty() -> error
            !valid(text.orEmpty()) -> resources.getString(R.string.ps_error_invalid_card_number)
            else -> ""
        }
    }

    private fun valid(number: String): Boolean {
        val cleanNumber = stripSpaces(number)

        if (cleanNumber.length <= 1) return false
        if (containsNonDigitChars(cleanNumber)) return false

        return cleanNumber.reversed()
            .asSequence()
            .digits()
            .mapIndexed(::doubleEverySecondDigit)
            .map(::limitDigitsGreaterThanNine)
            .sum()
            .isDivisibleBy(10)
    }

    private fun stripSpaces(number: String) = number.filterNot(Char::isWhitespace)
    private fun containsNonDigitChars(number: String) = !number.all(Char::isDigit)
    private fun doubleEverySecondDigit(index: Int, digit: Int) = if (index.isOdd()) digit * 2 else digit
    private fun limitDigitsGreaterThanNine(digit: Int) = if (digit > 9) digit - 9 else digit

    private fun Sequence<Char>.digits() = this.map(Character::getNumericValue)
    private fun Int.isOdd() = this.rem(2) == 1
    private fun Int.isDivisibleBy(num: Int) = this.rem(num) == 0
}