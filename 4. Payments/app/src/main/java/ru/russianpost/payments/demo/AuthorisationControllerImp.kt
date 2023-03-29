package ru.russianpost.payments.demo

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import ru.russianpost.android.protocols.auth.AuthState
import ru.russianpost.android.protocols.auth.AuthorisationController
import kotlin.concurrent.thread

class AuthorisationControllerImp(private val context: Context): AuthorisationController {
    private var auth: AuthState = AuthState.AUTHENTICATED//NOT_AUTH
    private var listener: ((authState: AuthState) -> Unit)? = null

    override fun getAuthState(): AuthState {
        return auth
    }

    override fun initAuth() {
        Handler(context.mainLooper).post {
            context.startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse("https://google.com")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
/*
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://google.com")
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
*/
        }

        thread {
            Thread.sleep(5000)
            auth = AuthState.AUTHENTICATED
            listener?.invoke(AuthState.AUTHENTICATED)
        }
    }

    override fun setAuthChangedListener(listener: ((authState: AuthState) -> Unit)?) {
        this.listener = listener
    }
}