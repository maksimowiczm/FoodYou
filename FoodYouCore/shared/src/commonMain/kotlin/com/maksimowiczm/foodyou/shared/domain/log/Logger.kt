package com.maksimowiczm.foodyou.shared.domain.log

import com.maksimowiczm.foodyou.shared.domain.Err
import com.maksimowiczm.foodyou.shared.domain.Result

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

inline fun <reified E> Logger.logAndReturnFailure(
    tag: String,
    throwable: Throwable?,
    error: E,
    noinline message: () -> String,
): Result<Nothing, E> {
    w(tag, throwable, message)
    w(tag) { "Error: $error" }
    return Err(error)
}
