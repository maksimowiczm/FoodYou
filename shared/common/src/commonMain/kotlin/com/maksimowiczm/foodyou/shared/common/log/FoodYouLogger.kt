package com.maksimowiczm.foodyou.shared.common.log

interface Logger {
    fun d(tag: String, throwable: Throwable? = null, message: () -> String)

    fun d(tag: String, message: () -> String) = d(tag, null, message)

    fun w(tag: String, throwable: Throwable? = null, message: () -> String)

    fun w(tag: String, message: () -> String) = w(tag, null, message)

    fun e(tag: String, throwable: Throwable? = null, message: () -> String)

    fun e(tag: String, message: () -> String) = e(tag, null, message)

    fun i(tag: String, throwable: Throwable? = null, message: () -> String)

    fun i(tag: String, message: () -> String) = i(tag, null, message)
}

object FoodYouLogger : Logger {

    private val kermit = co.touchlab.kermit.Logger

    override fun d(tag: String, throwable: Throwable?, message: () -> String) {
        kermit.d(throwable, tag, message)
    }

    override fun w(tag: String, throwable: Throwable?, message: () -> String) {
        kermit.w(throwable, tag, message)
    }

    override fun e(tag: String, throwable: Throwable?, message: () -> String) {
        kermit.e(throwable, tag, message)
    }

    override fun i(tag: String, throwable: Throwable?, message: () -> String) {
        kermit.i(throwable, tag, message)
    }
}
