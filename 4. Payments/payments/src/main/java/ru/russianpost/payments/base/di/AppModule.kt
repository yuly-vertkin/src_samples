package ru.russianpost.payments.base.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.russianpost.payments.base.ui.BASE_URL
import ru.russianpost.payments.data.network.*
import ru.russianpost.payments.data.network.AutoFinesService
import ru.russianpost.payments.data.network.HistoryService
import ru.russianpost.payments.data.network.PaymentCardService
import ru.russianpost.payments.data.network.TaxService
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.AppContextProviderImp

@Module
@InstallIn(ActivityRetainedComponent::class)
internal class AppModule {
    @ActivityRetainedScoped
    @Provides
    fun provideAppContextProvider(@ApplicationContext appContext: Context) : AppContextProvider {
        return AppContextProviderImp(appContext)
    }

    @ActivityRetainedScoped
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @ActivityRetainedScoped
    @Provides
    fun provideTaxService(retrofit: Retrofit): TaxService =
        retrofit.create(TaxService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun providePaymentCardService(retrofit: Retrofit): PaymentCardService =
        retrofit.create(PaymentCardService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideAutoFinesService(retrofit: Retrofit): AutoFinesService =
        retrofit.create(AutoFinesService::class.java)

    @ActivityRetainedScoped
    @Provides
    fun provideHistoryService(retrofit: Retrofit): HistoryService =
        retrofit.create(HistoryService::class.java)
}
