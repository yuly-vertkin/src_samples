package ru.russianpost.payments.base.domain

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.russianpost.payments.base.ui.SUM_FRACTION_NUMBER
import ru.russianpost.payments.base.ui.TAX_PERIOD_PREFIX

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class BaseInputFieldFormatterTest {
    @Test
    fun testNumberFormat() {
        val number = 3
        val formatter = BaseInputFieldFormatter(number)
        var expected = "123"
        var result = formatter.format("123")
        assertEquals(expected, result)
        result = formatter.format("1234567890")
        assertEquals(expected, result)
        expected = "1"
        result = formatter.format("1")
        assertEquals(expected, result)
        expected = ""
        result = formatter.format("")
        assertEquals(expected, result)
        result = formatter.format(null)
        assertEquals(expected, result)
    }

    @Test
    fun testNoNumber() {
        val formatter = BaseInputFieldFormatter()
        var expected = "123"
        var result = formatter.format("123")
        assertEquals(expected, result)
        expected = ""
        result = formatter.format("")
        assertEquals(expected, result)
        result = formatter.format(null)
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class TextOnlyFieldFormatterTest {
    @Test
    fun testTextOnly() {
        val formatter = TextOnlyFieldFormatter()
        var expected = "абв"
        var result = formatter.format("абв")
        assertEquals(expected, result)
        result = formatter.format("1а2б3в4")
        assertEquals(expected, result)
        expected = ""
        result = formatter.format("abc123_+!?/")
        assertEquals(expected, result)
        expected = "а б-в"
        result = formatter.format("а б-в")
        assertEquals(expected, result)
        result = formatter.format("аa 2б-bв")
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class TaxPeriodFieldFormatterTest {
    @Test
    fun testTaxPeriod() {
        val formatter = TaxPeriodFieldFormatter()
        var expected = TAX_PERIOD_PREFIX + "2020"
        var result = formatter.format(TAX_PERIOD_PREFIX + "2020")
        assertEquals(expected, result)
        result = formatter.format(TAX_PERIOD_PREFIX + "20201")
        assertEquals(expected, result)
        result = formatter.format(TAX_PERIOD_PREFIX + "2020abc")
        assertEquals(expected, result)
        expected = TAX_PERIOD_PREFIX
        result = formatter.format(TAX_PERIOD_PREFIX)
        assertEquals(expected, result)
        result = formatter.format(TAX_PERIOD_PREFIX + "abcd2020")
        assertEquals(expected, result)
        expected = TAX_PERIOD_PREFIX + "0"
        result = formatter.format(TAX_PERIOD_PREFIX + "0")
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class TemplateFieldFormatterTest {
    @Test
    fun template() {
        val formatter = TemplateFieldFormatter("##-##")
        var expected = "12-34"
        var result = formatter.format("12-34")
        assertEquals(expected, result)
        result = formatter.format("1234")
        assertEquals(expected, result)
        result = formatter.format("123456")
        assertEquals(expected, result)
        result = formatter.format("1a2b3c4d")
        assertEquals(expected, result)
        expected = ""
        result = formatter.format("ab-cd")
        assertEquals(expected, result)
        result = formatter.format("abcd")
        assertEquals(expected, result)
    }

    @Test
    fun noTemplate() {
        val formatter = TemplateFieldFormatter("")
        var expected = ""
        var result = formatter.format("12-34")
        assertEquals(expected, result)
        result = formatter.format("")
        assertEquals(expected, result)
        result = formatter.format(null)
        assertEquals(expected, result)
    }
}


@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class DecimalNumberFormatterTest {
    @Test
    fun testTaxPeriod() {
        val formatter = DecimalNumberFormatter(SUM_FRACTION_NUMBER)
        var expected = "1.23"
        var result = formatter.format("1.23")
        assertEquals(expected, result)
        result = formatter.format("1.234567890")
        assertEquals(expected, result)
        expected = "0.00"
        result = formatter.format("0.00")
        assertEquals(expected, result)
        result = formatter.format("0.00123")
        assertEquals(expected, result)
    }
}
