package ru.russianpost.payments.base.ui

import android.annotation.SuppressLint
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.fragment.app.viewModels
import ru.russianpost.payments.PaymentContract
import ru.russianpost.payments.R
import ru.russianpost.payments.databinding.PsFragmentWebViewBinding

internal class SimpleWebViewFragment : BaseFragment<PsFragmentWebViewBinding, SimpleWebViewViewModel>() {
    override val viewModel: SimpleWebViewViewModel by viewModels()
    override val layoutRes = R.layout.ps_fragment_web_view

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.apply {
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {
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
}