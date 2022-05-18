package ru.russianpost.payments.tools

import android.view.View
import androidx.annotation.StringRes
import ru.russianpost.mobileapp.widget.Snackbar
import ru.russianpost.mobileapp.widget.Snackbar.Style

internal fun String.toIntOrDefault(default: Int): Int =
    this.toIntOrNull() ?: default

internal fun String.toFloatOrDefault(default: Float): Float =
    this.toFloatOrNull() ?: default

internal data class SnackbarParams(
    @StringRes val resId: Int,
    val style: Style = Style.DEFAULT
)

internal fun View.showSnackbar(params: SnackbarParams) =
    Snackbar.make(this, params.resId, params.style).show()
