package ru.russianpost.payments.features.payment_card.ui

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.fragment.app.viewModels
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.databinding.PsFragmentCardPaymentBinding

/**
 * Экран оплаты картой
 */
internal class PaymentCardFragment : BaseFragment<PsFragmentCardPaymentBinding, PaymentCardViewModel>() {
    override val viewModel: PaymentCardViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_card_payment

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.apply {
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                    return if (viewModel.onUrlLoading(request?.url.toString()))
                        WebResourceResponse("text/html", "UTF-8", context.assets.open(EMPTY_PAGE_ASSET))
                    else
                        super.shouldInterceptRequest(view, request)
                }

// TODO: в процессе выбора между shouldInterceptRequest и onPageStarted

//                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
//                    super.onPageStarted(view, url, favicon)
//                    viewModel.onUrlLoading(url.orEmpty())
//                }

// TODO: shouldOverrideUrlLoading  не работает т.к.
// видимо используется 307 Temporary Redirect -
// Метод и тело исходного запроса повторно используются для выполнения перенаправленного запроса.
// Если вы хотите, чтобы используемый метод был изменён на GET, используйте 303
// https://developer.mozilla.org/ru/docs/Web/HTTP/Status/307

//                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?) : Boolean {
//                    return viewModel.onUrlLoading(request?.url.toString())
//                }

                @SuppressLint("WebViewClientOnReceivedSslError")
                override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
                    val shouldProceedOnSslError = PaymentContract.isTestVersion
                    if (shouldProceedOnSslError) {
                        handler?.proceed()
                    } else {
                        super.onReceivedSslError(view, handler, error)
                    }
                }
            }
        }
    }

    companion object {
        private const val EMPTY_PAGE_ASSET = "demo/empty_page.html"
    }
}