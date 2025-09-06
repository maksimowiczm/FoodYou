package com.maksimowiczm.foodyou.shared.common.application.log

import com.maksimowiczm.foodyou.core.shared.log.Logger

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
