package ru.russianpost.payments.entities

import android.content.Context

internal interface AppContextProvider {
    fun getContext() : Context
}

internal class AppContextProviderImp(private val appContext: Context) : AppContextProvider {
    override fun getContext() = appContext
}

