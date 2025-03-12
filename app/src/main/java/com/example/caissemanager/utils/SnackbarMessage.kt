package com.example.caissemanager.utils

import android.content.res.Resources
import androidx.annotation.StringRes
import com.example.caissemanager.R


sealed class SnackbarMessage {


    class StringSnackbar(val message: String) : SnackbarMessage()

    class ResourceSnackbar(@StringRes val message: Int) : SnackbarMessage()


    companion object {


        fun SnackbarMessage.toMessage(resource: Resources): String {

            return when (this) {

                is StringSnackbar -> this.message
                is ResourceSnackbar -> resource.getString(this.message)
                else -> ""
            }
        }

        fun Throwable.toSnackbarMessage(): SnackbarMessage {

            val message = this.message.orEmpty()
            return if (message.isNotBlank()) StringSnackbar(message)
            else ResourceSnackbar(R.string.generic_error)
        }
    }

}
