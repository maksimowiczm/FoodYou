package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.common.log.Logger

expect object FoodYouLogger : Logger {
    override fun d(tag: String, throwable: Throwable?, message: () -> String)

    override fun w(tag: String, throwable: Throwable?, message: () -> String)

    override fun e(tag: String, throwable: Throwable?, message: () -> String)

    override fun i(tag: String, throwable: Throwable?, message: () -> String)
}
