package ru.russianpost.payments.base.di

import dagger.Binds
import dagger.Module
import ru.russianpost.payments.base.domain.PaymentStartParamsRepository
import ru.russianpost.payments.data.repositories.*
import ru.russianpost.payments.features.advices.domain.AdvicesRepository
import ru.russianpost.payments.features.charges.domain.ChargesRepository
import ru.russianpost.payments.features.history.domain.HistoryRepository
import ru.russianpost.payments.features.payment_card.domain.PaymentCardRepository
import ru.russianpost.payments.features.tax.domain.TaxDetailsRepository

@Module
internal abstract class RepositoryModule {
    @PaymentScope
    @Binds
    abstract fun bindPaymentStartParamsRepository(
        repositoryImpl: PaymentStartParamsRepositoryImp
    ): PaymentStartParamsRepository

    @PaymentScope
    @Binds
    abstract fun bindAdvicesRepository(
        repositoryImpl: AdvicesRepositoryImp
    ): AdvicesRepository

    @PaymentScope
    @Binds
    abstract fun bindHistoryRepository(
        repositoryImpl: HistoryRepositoryImp
    ): HistoryRepository

    @PaymentScope
    @Binds
    abstract fun bindTaxDetailsRepository(
        repositoryImpl: TaxDetailsRepositoryImp
    ): TaxDetailsRepository

    @PaymentScope
    @Binds
    abstract fun bindChargesRepository(
        repositoryImpl: ChargesRepositoryImp
    ): ChargesRepository

    @PaymentScope
    @Binds
    abstract fun bindPaymentCardRepository(
        repositoryImpl: PaymentCardRepositoryImp
    ): PaymentCardRepository
}