package ru.russianpost.payments.base.domain

import ru.russianpost.payments.base.ui.TAX_PERIOD_LENGTH
import ru.russianpost.payments.base.ui.TAX_PERIOD_PREFIX

internal open class BaseInputFieldFormatter(
    protected val number: Int = 0
) {
    open fun format(text: String?) : String {
        val str = text.orEmpty()
        return when {
            number != 0 && str.length > number -> str.substring(0 until number)
            else -> str
        }
    }
}

internal class TextOnlyFieldFormatter(
    number: Int = 0
) : BaseInputFieldFormatter(number) {
    private val template = ('а'..'я').toList() + ('А'..'Я').toList() + "ёЁ -".toList()

    override fun format(_text: String?) : String {
        val text = super.format(_text)
        val newText = StringBuilder()
        text.forEach { c ->
            if (template.contains(c)) {
                newText.append(c)
            }
        }
        return newText.toString()
    }
}

internal class TaxPeriodFieldFormatter(
) : BaseInputFieldFormatter(TAX_PERIOD_LENGTH) {
    override fun format(_text: String?) : String {
        val text = super.format(_text)
        val newText = StringBuilder(TAX_PERIOD_PREFIX)
        if (text.length > TAX_PERIOD_PREFIX.length) {
            text.substring(TAX_PERIOD_PREFIX.length).forEach { c ->
                if (c.isDigit()) {
                    newText.append(c)
                }
            }
        }
        return newText.toString()
    }
}

internal class TemplateFieldFormatter(
    private val template: String,
) : BaseInputFieldFormatter() {
    override fun format(_text: String?) : String {
        val text = super.format(_text)
        val newText = StringBuilder()

        var i = 0
        var j = 0
        while (i < text.length && j < template.length) {
            if (!text[i].isDigit()) {
                i++
            } else if (template[j] != '#') {
                newText.append(template[j++])
            } else {
                newText.append(text[i++])
                j++
            }
        }
        return newText.toString()
    }
}

internal class DecimalNumberFormatter(
    private val fractionNum: Int,
) : BaseInputFieldFormatter() {
    override fun format(_text: String?) : String {
        val text = super.format(_text)

        return text.indexOf('.').let {
            val endLength = it + 1 + fractionNum
            if (it != -1 && endLength < text.length) {
                text.substring(0, endLength)
            } else {
                text
            }
        }
    }
}
