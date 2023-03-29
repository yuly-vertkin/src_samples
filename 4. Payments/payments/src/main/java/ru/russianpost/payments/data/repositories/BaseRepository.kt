package ru.russianpost.payments.data.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ru.russianpost.payments.base.ui.ERROR_NO_NETWORK
import ru.russianpost.payments.base.ui.ERROR_POOLING
import ru.russianpost.payments.base.ui.NO_ERROR
import ru.russianpost.payments.base.ui.POOLING_TIMEOUT
import ru.russianpost.payments.data.network.isNetworkAvailable
import ru.russianpost.payments.entities.AppContextProvider
import ru.russianpost.payments.entities.BaseResponse
import ru.russianpost.payments.entities.ResponseException
import ru.russianpost.payments.entities.Result
import ru.russianpost.payments.tools.Log
import java.lang.reflect.Type

internal open class BaseRepository<T>(
    appContextProvider: AppContextProvider,
    private val gson: Gson
) {
    protected val context = appContextProvider.getContext()

    @Volatile
    private var cache: T? = null

    open fun getData() : T? = cache

    open fun saveData(data: T?) {
        cache = data
    }

    protected fun <R> callAction(action: suspend () -> R) : Flow<Result<R>> = flow {
        emit(Result.Loading)
        try {
            var result: R
            var errorCode: Int

// for testing
//            delay(5000)

            do {
                result = action()
                val error = if (result is List<*>) result.firstOrNull() as? BaseResponse else result as? BaseResponse
                errorCode = error?.errorCode ?: NO_ERROR

                when (errorCode) {
                    NO_ERROR -> emit(Result.Success(result))
                    ERROR_POOLING -> delay(POOLING_TIMEOUT.toLong())
                    else -> throw ResponseException(errorCode, error?.errorTitle, error?.errorMessage)
                }
            } while (errorCode == ERROR_POOLING)
        } catch (ex: Exception) {
            val e = if (ex !is ResponseException && !isNetworkAvailable())
                        ResponseException(ERROR_NO_NETWORK)
                    else ex
            Log.e(e)
            emit(Result.Error(e))
        }
    }

    protected fun getBooleanPreference(key: String) =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getBoolean(key, false)

    protected fun setBooleanPreference(key: String, value: Boolean?) {
        value?.let {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putBoolean(key, it)
                apply()
            }
        }
    }

    protected fun getIntPreference(key: String) =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(key, 0)

    protected fun setIntPreference(key: String, value: Int) {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    protected fun getStringPreference(key: String) =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(key, null)

    protected fun setStringPreference(key: String, value: String?) {
        value?.let {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString(key, it)
                apply()
            }
        }
    }

    protected fun getStringSetPreference(key: String): Set<String>? =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getStringSet(key, emptySet())

    protected fun setStringSetPreference(key: String, value: Set<String>?) {
        value?.let {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putStringSet(key, it)
                apply()
            }
        }
    }

    protected fun getStringListPreference(key: String) : List<String>? {
        val json: String? = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(key, null)
        val type: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return try { gson.fromJson(json, type) }
               catch (e: Exception) { null }
    }

    protected fun setStringListPreference(key: String, value: List<String>?) {
        val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        with (sharedPref.edit()) {
            try {
                putString(key, gson.toJson(value))
                apply()
            } catch (e: Exception) {}
        }
    }

    protected fun <T> readFromAsset(name: String, classOfT: Class<T>) : T? {
        return try {
            gson.fromJson(context.assets.open(name).reader(), classOfT)
        } catch (ex: Exception) {
            null
        }
    }

    // temp for form asset file
    protected fun <T> tempWriteAsset(value: T) : String {
        return try {
            gson.toJson(value)
        } catch (ex: Exception) {
            ""
        }
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "ru.russianpost.payments.PREFERENCE_FILE_NAME"
        private const val USER_ID_KEY = "USER_ID_KEY"

        fun getUserIdPreference(context: Context) =
            context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(USER_ID_KEY, null)

        fun clearOldUserPreferences(context: Context, value: String?) {
            val sharedPref = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                clear()
                putString(USER_ID_KEY, value)
                apply()
            }
        }
    }
}