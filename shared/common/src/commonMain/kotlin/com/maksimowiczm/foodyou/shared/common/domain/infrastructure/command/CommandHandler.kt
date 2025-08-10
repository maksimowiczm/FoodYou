package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command

import com.maksimowiczm.foodyou.shared.common.domain.result.Result

interface CommandHandler<in C : Command<R, E>, R, E> {
    suspend fun handle(command: C): Result<R, E>
}
