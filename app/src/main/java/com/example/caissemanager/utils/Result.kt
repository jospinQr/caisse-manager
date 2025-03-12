package com.example.caissemanager.utils

sealed class Result<out T>(val data: T? = null, val e: Throwable? = null) {
    object Loading : Result<Nothing>()
    class Succes<T>(data: T?) : Result<T>(data)
    class Error<T>(private val message: Throwable) : Result<T>(e = message)

}