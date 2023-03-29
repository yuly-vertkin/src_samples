package ru.russianpost.payments.base.di

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.Reusable
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds
import ru.russianpost.payments.base.ui.MainViewModel
import ru.russianpost.payments.features.auto_fines.ui.FineMenuViewModel
import ru.russianpost.payments.base.ui.SimpleWebViewViewModel
import ru.russianpost.payments.features.advices.ui.AdviceViewModel
import ru.russianpost.payments.features.auto_fines.ui.*
import ru.russianpost.payments.features.history.ui.HistoryFilterViewModel
import ru.russianpost.payments.features.history.ui.HistoryViewModel
import ru.russianpost.payments.features.payment_card.ui.*
import ru.russianpost.payments.features.payment_card.ui.AuthDialogViewModel
import ru.russianpost.payments.features.payment_card.ui.PaymentCardViewModel
import ru.russianpost.payments.features.payment_card.ui.PaymentDoneDialogViewModel
import ru.russianpost.payments.features.payment_card.ui.SelectCardDialogViewModel
import ru.russianpost.payments.features.payment_card.ui.ViewCheckViewModel
import ru.russianpost.payments.features.scan.ui.ScanViewModel
import ru.russianpost.payments.features.tax.ui.*
import ru.russianpost.payments.features.uid_tax.ui.*
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

interface AssistedSavedStateViewModelFactory<T : ViewModel> {
    fun create(savedStateHandle: SavedStateHandle): T
}

@Module
abstract class CommonUiModule {
    @Multibinds
    abstract fun viewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards ViewModel>

    @Multibinds
    abstract fun assistedViewModels(): Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>
}

@Reusable
class InjectingSavedStateViewModelFactory @Inject constructor(
    private val assistedFactories: Map<Class<out ViewModel>, @JvmSuppressWildcards AssistedSavedStateViewModelFactory<out ViewModel>>,
    private val viewModelProviders: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    /**
     * Creates instance of ViewModel either annotated with @AssistedInject or @Inject and passes dependencies it needs.
     */
    fun create(owner: SavedStateRegistryOwner, defaultArgs: Bundle? = null) =
        object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
            override fun <T : ViewModel?> create(
                key: String,
                modelClass: Class<T>,
                handle: SavedStateHandle
            ): T {
                val viewModel =
                    createAssistedInjectViewModel(modelClass, handle)
                        ?: createInjectViewModel(modelClass)
                        ?: throw IllegalArgumentException("Unknown model class $modelClass")

                try {
                    @Suppress("UNCHECKED_CAST")
                    return viewModel as T
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

    /**
     * Creates ViewModel based on @AssistedInject constructor and its factory
     */
    private fun <T : ViewModel?> createAssistedInjectViewModel(
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): ViewModel? {
        val creator = assistedFactories[modelClass]
            ?: assistedFactories.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: return null

        return creator.create(handle)
    }

    /**
     * Creates ViewModel based on regular Dagger @Inject constructor
     */
    private fun <T : ViewModel?> createInjectViewModel(modelClass: Class<T>): ViewModel? {
        val creator = viewModelProviders[modelClass]
            ?: viewModelProviders.asIterable().firstOrNull { modelClass.isAssignableFrom(it.key) }?.value
            ?: return null

        return creator.get()
    }
}

@Module
internal abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun mainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AdviceViewModel::class)
    abstract fun adviceViewModel(viewModel: AdviceViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SimpleWebViewViewModel::class)
    abstract fun webViewModel(viewModel: SimpleWebViewViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

// uin tax
    @Binds
    @IntoMap
    @ViewModelKey(UidTaxMenuViewModel::class)
    abstract fun uidTaxMenuViewModel(viewModel: UidTaxMenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxSearchViewModel::class)
    abstract fun uidTaxSearchViewModel(viewModel: UidTaxSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxUinSearchViewModel::class)
    abstract fun uidTaxUinSearchViewModel(viewModel: UidTaxUinSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxesViewModel::class)
    abstract fun uidTaxesViewModel(factory: UidTaxesViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxSettingsViewModel::class)
    abstract fun uidTaxSettingsViewModel(viewModel: UidTaxSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxDocumentsViewModel::class)
    abstract fun uidTaxDocumentsViewModel(viewModel: UidTaxDocumentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxAddDocumentViewModel::class)
    abstract fun uidTaxAddDocumentViewModel(viewModel: UidTaxAddDocumentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxEditDocumentViewModel::class)
    abstract fun uidTaxEditDocumentViewModel(factory: UidTaxEditDocumentViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxDetailViewModel::class)
    abstract fun uidTaxDetailViewModel(factory: UidTaxDetailViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxRequisitesViewModel::class)
    abstract fun uidTaxRequisitesViewModel(factory: UidTaxRequisitesViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxConfirmationViewModel::class)
    abstract fun uidTaxConfirmationViewModel(viewModel: UidTaxConfirmationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(UidTaxPaymentDoneViewModel::class)
    abstract fun uidTaxPaymentDoneViewModel(factory: UidTaxPaymentDoneViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

// tax
    @Binds
    @IntoMap
    @ViewModelKey(TaxPayerInfoViewModel::class)
    abstract fun taxPayerInfoViewModel(viewModel: TaxPayerInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaxConfirmationViewModel::class)
    abstract fun taxConfirmationViewModel(viewModel: TaxConfirmationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaxPaymentDoneViewModel::class)
    abstract fun taxPaymentDoneViewModel(factory: TaxPaymentDoneViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(TaxPaymentInfoViewModel::class)
    abstract fun taxPaymentInfoViewModel(viewModel: TaxPaymentInfoViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaxRecipientInfoViewModel::class)
    abstract fun taxRecipientInfoViewModel(viewModel: TaxRecipientInfoViewModel): ViewModel

// auto_fines
    @Binds
    @IntoMap
    @ViewModelKey(FineMenuViewModel::class)
    abstract fun fineMenuViewModel(viewModel: FineMenuViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FineSearchViewModel::class)
    abstract fun fineSearchViewModel(viewModel: FineSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FineUinSearchViewModel::class)
    abstract fun fineUinSearchViewModel(viewModel: FineUinSearchViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FinesViewModel::class)
    abstract fun finesViewModel(factory: FinesViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FineSettingsViewModel::class)
    abstract fun fineSettingsViewModel(viewModel: FineSettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FineDocumentsViewModel::class)
    abstract fun fineDocumentsViewModel(viewModel: FineDocumentsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FineAddDocumentDialogViewModel::class)
    abstract fun fineAddDocumentDialogViewModel(viewModel: FineAddDocumentDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FineAddDocumentViewModel::class)
    abstract fun fineAddDocumentViewModel(factory: FineAddDocumentViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FineEditDocumentViewModel::class)
    abstract fun fineEditDocumentViewModel(factory: FineEditDocumentViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FineDetailViewModel::class)
    abstract fun fineDetailViewModel(factory: FineDetailViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FineRequisitesViewModel::class)
    abstract fun fineRequisitesViewModel(factory: FineRequisitesViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

    @Binds
    @IntoMap
    @ViewModelKey(FineConfirmationViewModel::class)
    abstract fun fineConfirmationViewModel(viewModel: FineConfirmationViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FinePaymentDoneViewModel::class)
    abstract fun finePaymentDoneViewModel(factory: FinePaymentDoneViewModel.Factory): AssistedSavedStateViewModelFactory<out ViewModel>

// payment_card
    @Binds
    @IntoMap
    @ViewModelKey(AuthDialogViewModel::class)
    abstract fun authDialogViewModel(viewModel: AuthDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddressDialogViewModel::class)
    abstract fun addressDialogViewModel(viewModel: AddressDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SelectCardDialogViewModel::class)
    abstract fun selectCardDialogViewModel(factory: SelectCardDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentCardViewModel::class)
    abstract fun paymentCardViewModel(viewModel: PaymentCardViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PaymentDoneDialogViewModel::class)
    abstract fun paymentDoneDialogViewModel(viewModel: PaymentDoneDialogViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ViewCheckViewModel::class)
    abstract fun viewCheckViewModel(viewModel: ViewCheckViewModel): ViewModel

// history
    @Binds
    @IntoMap
    @ViewModelKey(HistoryFilterViewModel::class)
    abstract fun historyFilterViewModel(viewModel: HistoryFilterViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(HistoryViewModel::class)
    abstract fun historyViewModel(viewModel: HistoryViewModel): ViewModel

    // scan
    @Binds
    @IntoMap
    @ViewModelKey(ScanViewModel::class)
    abstract fun scanViewModel(viewModel: ScanViewModel): ViewModel
}
