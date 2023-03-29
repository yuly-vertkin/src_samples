package ru.russianpost.payments.base.di

import android.content.Context
import androidx.databinding.ViewDataBinding
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Component
import dagger.Module
import dagger.Provides
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import ru.russianpost.android.protocols.auth.ExternalAuthProtocol
import ru.russianpost.android.protocols.http.RetrofitBuilder
import ru.russianpost.android.protocols.http.RetrofitOpt
import ru.russianpost.payments.base.ui.BaseBottomDialogFragment
import ru.russianpost.payments.base.ui.BaseDialogFragment
import ru.russianpost.payments.base.ui.BaseFragment
import ru.russianpost.payments.base.ui.BaseViewModel
import ru.russianpost.payments.data.network.*
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.AppContextProviderImp
import javax.inject.Scope

@Scope
@MustBeDocumented
@Retention(value = AnnotationRetention.RUNTIME)
annotation class PaymentScope


@PaymentScope
@Component(modules = [PaymentModule::class, ViewModelModule::class, RepositoryModule::class])
internal interface PaymentComponent {
    fun inject(fragment: BaseFragment<ViewDataBinding, BaseViewModel>)
    fun inject(fragment: BaseBottomDialogFragment<ViewDataBinding, BaseViewModel>)
    fun inject(fragment: BaseDialogFragment<ViewDataBinding, BaseViewModel>)
}


@Module
internal class PaymentModule(
    private val appContext: Context,
    private val retrofitBuilder: RetrofitBuilder,
    private val externalAuth: ExternalAuthProtocol,
    ) {
    @PaymentScope
    @Provides
    fun provideAppContextProvider() : AppContextProvider {
        return AppContextProviderImp(appContext)
    }

    @PaymentScope
    @Provides
    fun provideExternalAuthProtocol(): ExternalAuthProtocol = externalAuth

    @PaymentScope
    @Provides
    fun provideRetrofitOpt(gson: Gson): RetrofitOpt {
        return RetrofitOpt(converters = listOf(
            ScalarsConverterFactory.create(),
            GsonConverterFactory.create(gson)
        ))
    }

    @PaymentScope
    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @PaymentScope
    @Provides
    fun provideTaxService(retrofitOpt: RetrofitOpt): TaxService =
        retrofitBuilder.createRetrofitApi(TaxService::class.java, retrofitOpt)

    @PaymentScope
    @Provides
    fun providePaymentCardService(retrofitOpt: RetrofitOpt): PaymentCardService =
        retrofitBuilder.createRetrofitApi(PaymentCardService::class.java, retrofitOpt)

    @PaymentScope
    @Provides
    fun provideChargesService(retrofitOpt: RetrofitOpt): ChargesService =
        retrofitBuilder.createRetrofitApi(ChargesService::class.java, retrofitOpt)

    @PaymentScope
    @Provides
    fun provideHistoryService(retrofitOpt: RetrofitOpt): HistoryService =
        retrofitBuilder.createRetrofitApi(HistoryService::class.java, retrofitOpt)

    @PaymentScope
    @Provides
    fun provideAdvicesService(retrofitOpt: RetrofitOpt): AdvicesService =
        retrofitBuilder.createRetrofitApi(AdvicesService::class.java, retrofitOpt)
}
