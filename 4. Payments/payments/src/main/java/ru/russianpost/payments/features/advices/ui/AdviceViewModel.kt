package ru.russianpost.payments.features.advices.ui

import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.advices.AdviceData
import ru.russianpost.payments.features.advices.domain.AdvicesRepository
import ru.russianpost.payments.tools.SnackbarParams
import javax.inject.Inject

/**
 * ViewModel подсказки
 */
internal class AdviceViewModel @Inject constructor(
    private val repository: AdvicesRepository,
    appContextProvider: AppContextProvider,
) : BaseViewModel(appContextProvider) {
    private var adviceId: String? = null

    fun setStartParams(adviceId: String) {
        this.adviceId = adviceId

        processNetworkCall(
            action = { repository.getAdvices() },
            onSuccess = ::showAdvice,
            onError = { showSnackbar.value = SnackbarParams(R.string.ps_error_service_unavailable) },
        )
    }

    private fun showAdvice(advices: List<AdviceData>) {
        val advice = advices.firstOrNull { it.id == adviceId }
//            ?.copy(link = "https://google.com")

        with(context.resources) {
            advice?.let {
                addFields(listOf(
                    TextFieldValue(
                        text = it.title,
                        textSize = getDimension(R.dimen.ps_text_size_20sp),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                    ),
                    ImageFieldValue(
                        loadUrl = MutableLiveData(PaymentContract.endPoint + it.img),
                    ),
                    TextFieldValue(
                        text = it.desc,
                        textSize = getDimension(R.dimen.ps_text_size_16sp),
                        textColor = ContextCompat.getColor(context, R.color.grayscale_carbon),
                        horizontalMarginRes = R.dimen.ps_horizontal_margin,
                        verticalMarginRes = R.dimen.ps_dimen_16dp,
                    ),
                ))

                it.link?.let { url ->
                    addField(
                        CellFieldValue(
                            title = url,
                            titleColorRes = R.color.common_xenon,
                            data = url,
                            action = ::onLinkClick,
                        ),
                    )
                }
            }

            addField(
                ButtonFieldValue(
                    text = MutableLiveData(getString(R.string.ps_understand)),
                    horizontalMarginRes = R.dimen.ps_horizontal_margin,
                    action = ::onButtonClick,
                ),
                isMainFields = false
            )
        }
    }

    private fun onLinkClick(data: Any?) {
        data?.let {
            action.value = AdviceFragmentDirections.webViewFragmentAction(it as String)
        }
    }

    private fun onButtonClick(data: Any?) {
        actionBack.value = true
    }
}