package ru.russianpost.payments.base.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.data.repositories.*
import ru.russianpost.payments.features.auto_fines.domain.AutoFinesRepository
import ru.russianpost.payments.features.history.domain.HistoryRepository
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository

@Module
@InstallIn(ActivityRetainedComponent::class)
internal abstract class RepositoryModule {
    @ActivityRetainedScoped
    @Binds
    abstract fun bindPaymentStartParamsRepository(
        repositoryImpl: PaymentStartParamsRepositoryImp
    ): PaymentStartParamsRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindHistoryRepository(
        repositoryImpl: HistoryRepositoryImp
    ): HistoryRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindTaxDetailsRepository(
        repositoryImpl: TaxDetailsRepositoryImp
    ): TaxDetailsRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindAutoFinesRepository(
        repositoryImpl: AutoFinesRepositoryImp
    ): AutoFinesRepository

    @ActivityRetainedScoped
    @Binds
    abstract fun bindPaymentCardRepository(
        repositoryImpl: PaymentCardRepositoryImp
    ): PaymentCardRepository
}