package com.maksimowiczm.foodyou.business.shared.application.command

import com.maksimowiczm.foodyou.shared.common.result.Result

interface CommandHandler<in C : Command<R, E>, R, E> {
    suspend fun handle(command: C): Result<R, E>
}
