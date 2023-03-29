package ru.russianpost.payments.base.domain

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.TAX_PERIOD_PREFIX
import java.util.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
internal class BaseInputFieldValidatorTest {
    private val resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun testNeedValidate() {
        val validator = BaseInputFieldValidator()
        var expected = resources.getString(R.string.ps_error_field_empty)
        var result = validator.validate(resources, null, true)
        assertEquals(expected, result)
        validator.isValidate = true
        result = validator.validate(resources, null)
        assertEquals(expected, result)
        validator.isValidate = false
        expected = ""
        result = validator.validate(resources, "")
        assertEquals(expected, result)
    }

    @Test
    fun testFieldEmpty() {
        val validator = BaseInputFieldValidator()
        var expected = resources.getString(R.string.ps_error_field_empty)
        var result = validator.validate(resources, null, true)
        assertEquals(expected, result)
        result = validator.validate(resources, "", true)
        assertEquals(expected, result)
    }

    @Test
    fun testCharNumber() {
        val number = 3
        val validator = BaseInputFieldValidator(number)
        var expected = resources.getQuantityString(R.plurals.ps_error_field_length, number, number)
        var result = validator.validate(resources, "1", true)
        assertEquals(expected, result)
        expected = ""
        result = validator.validate(resources, "123", true)
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
internal class EmptyFieldValidatorTest {
    private val resources: Resources =
        ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun testEmptyField() {
        val validator = EmptyFieldValidator()
        var expected = ""
        var result = validator.validate(resources, null, true)
        assertEquals(expected, result)
        result = validator.validate(resources, "")
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
internal class PrefixValidatorTest {
    private val resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun testPrefix() {
        val prefix = "03"
        val validator = PrefixValidator(prefix)
        var expected = resources.getString(R.string.ps_error_prefix, prefix)
        var result = validator.validate(resources, "0", true)
        assertEquals(expected, result)
        result = validator.validate(resources, "02", true)
        assertEquals(expected, result)
        result = validator.validate(resources, "0222222", true)
        assertEquals(expected, result)
        expected = ""
        result = validator.validate(resources, "03", true)
        assertEquals(expected, result)
        result = validator.validate(resources, "0301234567890", true)
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
internal class TaxPeriodFieldValidatorTest {
    private val resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun testPrefix() {
        val validator = TaxPeriodFieldValidator()
        var expected = resources.getString(R.string.ps_error_year_tax_period)
        var result = validator.validate(resources, TAX_PERIOD_PREFIX + "1999", true)
        assertEquals(expected, result)
        result = validator.validate(resources, TAX_PERIOD_PREFIX + "2345", true)
        assertEquals(expected, result)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        result = validator.validate(resources, TAX_PERIOD_PREFIX + (currentYear + 1), true)
        assertEquals(expected, result)
        expected = ""
        result = validator.validate(resources, TAX_PERIOD_PREFIX + "2000", true)
        assertEquals(expected, result)
        result = validator.validate(resources, TAX_PERIOD_PREFIX + currentYear, true)
        assertEquals(expected, result)
    }
}

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
internal class NonZeroNumberValidatorTest {
    private val resources: Resources = ApplicationProvider.getApplicationContext<Context>().resources

    @Test
    fun testPrefix() {
        val validator = NonZeroNumberValidator()
        var expected = resources.getString(R.string.ps_error_non_zero)
        var result = validator.validate(resources, "0", true)
        assertEquals(expected, result)
        result = validator.validate(resources, "0.00", true)
        assertEquals(expected, result)
        expected = ""
        result = validator.validate(resources, "03", true)
        assertEquals(expected, result)
        result = validator.validate(resources, "0.0001", true)
        assertEquals(expected, result)
    }
}