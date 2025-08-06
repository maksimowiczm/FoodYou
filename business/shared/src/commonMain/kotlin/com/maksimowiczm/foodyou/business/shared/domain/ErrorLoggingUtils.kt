package com.maksimowiczm.foodyou.business.shared.domain

import com.maksimowiczm.foodyou.shared.common.domain.result.Err
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import com.maksimowiczm.foodyou.shared.common.log.FoodYouLogger

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
