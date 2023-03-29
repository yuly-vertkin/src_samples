package ru.russianpost.payments.demo

import android.content.Context
import android.content.Intent
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol.ExtAuthResult

class ExternalAuthProtocolImp(private val context: Context) : ExternalAuthProtocol {
    override fun createAuthIntent(): Intent =
        createAuthIntent("")

    override fun createAuthIntent(options: String): Intent =
        Intent(context, ExtAuthActivity::class.java)

    override fun extractAuthResult(resultData: Intent) : ExtAuthResult =
        ExtAuthResult(
            accessToken = "ESIA accessToken",
            idToken = "ESIA idToken",
        )
}
