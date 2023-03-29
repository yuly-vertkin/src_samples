package ru.russianpost.payments.base.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.robolectric.annotation.Config
import ru.russianpost.payments.R
import ru.russianpost.payments.base.domain.BaseInputFieldValidator
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.AppContextProviderImp
import ru.russianpost.payments.tools.CustomSpinnerAdapter

@RunWith(AndroidJUnit4::class)
@Config(sdk = [29])
class BaseViewModelTest {
    private lateinit var viewModel: BaseViewModel
    private val context: Context = ApplicationProvider.getApplicationContext<Application>()

    @Before
    fun setUp() {
        viewModel = TestViewModel(AppContextProviderImp(context))
    }

    @Test
    fun clearFields() {
        viewModel.addFields(listOf(
            CellFieldValue(
                id = R.id.ps_test1,
                title = TEST_TEXT,
            ),
            ContainerFieldValue(
                id = R.id.ps_container1,
                items = listOf(
                    InputFieldValue(
                        id = R.id.ps_test2,
                        text = MutableLiveData(TEST_TEXT),
                    ),
                    InputFieldValue(
                        id = R.id.ps_test3,
                        text = MutableLiveData(TEST_TEXT),
                    ),
                ),
            ),
        ))
        viewModel.addField(
            ButtonFieldValue(
                id = R.id.ps_test4,
                text = MutableLiveData(TEST_TEXT),
                isExtendedButton = true,
            ),
            isMainFields = false
        )

        viewModel.clearFields()
        viewModel.clearFields(false)

        assert(viewModel.getFieldSize() == 0)
        assert(viewModel.getFieldSize(false) == 0)
    }

    @Test
    fun getFields() {
        val cell = CellFieldValue(
            id = R.id.ps_test1,
            title = TEST_TEXT,
        )
        val input1 = InputFieldValue(
            id = R.id.ps_test2,
            text = MutableLiveData(TEST_TEXT),
        )
        val input2 = InputFieldValue(
            id = R.id.ps_test3,
            text = MutableLiveData(TEST_TEXT),
        )
        val button = ButtonFieldValue(
            id = R.id.ps_test4,
            text = MutableLiveData(TEST_TEXT),
            isExtendedButton = true,
        )

        viewModel.addFields(listOf(
            cell,
            ContainerFieldValue(
                id = R.id.ps_container1,
                items = listOf(
                    input1,
                    input2,
                ),
            ),
        ))
        viewModel.addField(
            button,
            isMainFields = false
        )

        assertSame(cell, viewModel.get<CellFieldValue>(R.id.ps_test1))
        assertSame(input1, viewModel.get<InputFieldValue>(R.id.ps_test2))
        assertSame(input2, viewModel.get<InputFieldValue>(R.id.ps_test3))
        assertSame(button, viewModel.get<ButtonFieldValue>(R.id.ps_test4, false))
    }

    @Test
    fun getFieldText() {
        viewModel.addFields(listOf(
            InputFieldValue(
                id = R.id.ps_test1,
                text = MutableLiveData(TEST_TEXT),
            ),
            SpinnerFieldValue(
                id = R.id.ps_test2,
                adapter = CustomSpinnerAdapter(context, R.layout.ps_spinner_dropdown_item),
                selected = MutableLiveData(TEST_TEXT),
                items = listOf(TEST_TEXT),
            ),
        ))
        viewModel.addField(
            ButtonFieldValue(
                id = R.id.ps_test3,
                text = MutableLiveData(TEST_TEXT),
                isExtendedButton = true,
            ),
            isMainFields = false
        )
        assertEquals(TEST_TEXT, viewModel.getFieldText(R.id.ps_test1))
        assertEquals(TEST_TEXT, viewModel.getFieldText(R.id.ps_test2))
        assertEquals(TEST_TEXT, viewModel.getFieldText(R.id.ps_test3, false))
    }

    @Test
    fun setFieldText() {
        viewModel.addFields(listOf(
            CellFieldValue(
                id = R.id.ps_test1,
            ),
            ContainerFieldValue(
                id = R.id.ps_container1,
                items = listOf(
                    InputFieldValue(
                        id = R.id.ps_test2,
                    ),
                    TextFieldValue(
                        id = R.id.ps_test3,
                        text = "",
                        textColor = 0,
                        textSize = 0f,
                    ),
                ),
            ),
        ))
        viewModel.addField(
            ButtonFieldValue(
                id = R.id.ps_test4,
                isExtendedButton = true,
            ),
            isMainFields = false
        )

        viewModel.setFieldText(R.id.ps_test1, TEST_TEXT)
        viewModel.setFieldText(R.id.ps_test2, TEST_TEXT)
        viewModel.setFieldText(R.id.ps_test3, TEST_TEXT)
        viewModel.setFieldText(R.id.ps_test4, TEST_TEXT, false)

        assertEquals(TEST_TEXT, viewModel.get<CellFieldValue>(R.id.ps_test1)?.subtitle?.value)
        assertEquals(TEST_TEXT, viewModel.get<InputFieldValue>(R.id.ps_test2)?.text?.value)
        assertEquals(TEST_TEXT, viewModel.get<TextFieldValue>(R.id.ps_test3)?.rightText?.value)
        assertEquals(TEST_TEXT, viewModel.get<ButtonFieldValue>(R.id.ps_test4, false)?.text?.value)
    }

    @Test
    fun setFieldError() {
        viewModel.addField(
            InputFieldValue(
                id = R.id.ps_test1,
            )
        )

        viewModel.setFieldError(R.id.ps_test1, TEST_TEXT)

        assertEquals(TEST_TEXT, viewModel.get<InputFieldValue>(R.id.ps_test1)?.error?.value)
    }

    @Test
    fun validateAllFalse() {
        val falseValidator = mock<BaseInputFieldValidator> {
            on { validate(any(), any(), eq(true)) } doReturn TEST_TEXT
        }
        val trueValidator = mock<BaseInputFieldValidator> {
            on { validate(any(), any(), eq(true)) } doReturn ""
        }
        viewModel.addFields(listOf(
            InputFieldValue(
                id = R.id.ps_test1,
                text = MutableLiveData(TEST_TEXT),
                validator = trueValidator
            ),
            InputFieldValue(
                id = R.id.ps_test2,
                text = MutableLiveData(TEST_TEXT),
                validator = falseValidator
            )
        ))

        val res = viewModel.validateAll(context.resources)
        assertFalse(res)
    }

    @Test
    fun validateAllTrue() {
        val trueValidator = mock<BaseInputFieldValidator> {
            on { validate(any(), any(), eq(true)) } doReturn ""
        }
        viewModel.addFields(listOf(
            InputFieldValue(
                id = R.id.ps_test1,
                text = MutableLiveData(TEST_TEXT),
                validator = trueValidator
            ),
            InputFieldValue(
                id = R.id.ps_test2,
                text = MutableLiveData(TEST_TEXT),
                validator = trueValidator
            )
        ))

        val res = viewModel.validateAll(context.resources)
        assertTrue(res)
    }

    companion object {
        const val TEST_TEXT = "test"
    }
}

internal class TestViewModel(appContextProvider: AppContextProvider) : BaseViewModel(appContextProvider)
