package com.maksimowiczm.foodyou.business.shared.application.infrastructure.error

import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import com.maksimowiczm.foodyou.shared.common.result.Err
import com.maksimowiczm.foodyou.shared.common.result.Result

object ErrorLoggingUtils {

    inline fun <reified E> logAndReturnFailure(
        tag: String,
        throwable: Throwable?,
        error: E,
        noinline message: () -> String,
    ): Result<Nothing, E> {
        FoodYouLogger.w(tag, throwable, message)
        FoodYouLogger.w(tag) { "Error: $error" }
        return Err(error)
    }
}
