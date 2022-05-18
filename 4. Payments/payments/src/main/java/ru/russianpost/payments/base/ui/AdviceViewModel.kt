package ru.russianpost.payments.base.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.russianpost.payments.R
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.Response
import ru.russianpost.payments.entities.auto_fines.AdviceData
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel подсказки
 */
@HiltViewModel
internal class AdviceViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: AutoFinesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {

    override fun onCreateView() {
        super.onCreateView()

        viewModelScope.launch {
            repository.getAdvices().collect {
                when(it) {
                    is Response.Success -> showAdvice(it.data)
                    is Response.Error -> showSnackbar.value = SnackbarParams(R.string.ps_error_service_unavailable)
                    else -> {}
                }
            }
        }
    }

    private fun showAdvice(advices: List<AdviceData>) {
        val advice: AdviceData? = when(savedStateHandle.get<String>(FRAGMENT_PARAMS_NAME)) {
            VRC_ADVICE -> advices.firstOrNull { it.id == VRC_ADVICE }
            DL_ADVICE -> advices.firstOrNull { it.id == DL_ADVICE }
            else -> null
        }

        with(context.resources) {
            advice?.let {
                addFields(listOf(
                    TextFieldValue(
                        text = it.title,
                        textSize = getDimension(R.dimen.ps_text_size_20sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                    ),
                    ImageFieldValue(
                        loadUrl = MutableLiveData(it.img /*"https://res.cloudinary.com/demo/image/upload/w_400/meowing_kitten.jpg"*/),
                    ),
                    TextFieldValue(
                        text = it.desc,
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                    ),
                ))
            }
            btnLabel.value = getString(R.string.ps_understand)
        }
    }

    override fun onButtonClick() {
        actionBack.value = true
    }
}