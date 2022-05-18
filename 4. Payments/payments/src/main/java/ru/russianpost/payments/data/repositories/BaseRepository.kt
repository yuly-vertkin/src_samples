package ru.russianpost.payments.data.repositories

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.russianpost.payments.entities.AppContextProvider
import java.lang.reflect.Type

internal open class BaseRepository<T>(
    appContextProvider: AppContextProvider,
    private val gson: Gson
) {
    companion object {
        private const val SHARED_PREFERENCES_NAME = "ru.russianpost.payments.PREFERENCE_FILE_NAME"
    }

    protected val context = appContextProvider.getContext()

    @Volatile
    private var cache: T? = null

    open fun getData() : T? = cache

    open fun saveData(data: T?) {
        cache = data
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

    protected fun getStringListPreference(key: String) : List<String>? {
        val json: String? = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .getString(key, null)
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
            gson.fromJson<T>(context.assets.open(name).reader(), classOfT)
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
}