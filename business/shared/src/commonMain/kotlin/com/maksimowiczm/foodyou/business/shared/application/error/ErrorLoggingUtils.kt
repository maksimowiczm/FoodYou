package com.maksimowiczm.foodyou.business.shared.application.error

import com.maksimowiczm.foodyou.shared.common.application.log.Logger
import com.maksimowiczm.foodyou.shared.common.result.Err
import com.maksimowiczm.foodyou.shared.common.result.Result

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
