package ru.russianpost.payments.demo

import retrofit2.Retrofit
import ru.russianpost.android.protocols.http.RetrofitBuilder
import ru.russianpost.android.protocols.http.RetrofitOpt

object RetrofitBuilderImp: RetrofitBuilder {
    const val BASE_URL = "https://opay.test.russianpost.ru"

    /*override*/ private fun createRetrofit(retrofitOpt: RetrofitOpt?): Retrofit {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("$BASE_URL/")
            .client(provideOkHttpClient())

        retrofitOpt?.converters?.forEach {
            retrofitBuilder.addConverterFactory(it)
        }
        return retrofitBuilder.build()
    }

    override fun <T> createRetrofitApi(apiClass: Class<T>, retrofitOpt: RetrofitOpt?): T {
        return createRetrofit(retrofitOpt).create(apiClass)
    }
}