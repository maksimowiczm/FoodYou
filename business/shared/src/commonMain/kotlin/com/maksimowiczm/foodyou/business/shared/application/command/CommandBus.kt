package com.maksimowiczm.foodyou.business.shared.application.command

import com.maksimowiczm.foodyou.shared.common.result.Result

interface CommandBus {
    suspend fun <R, E> dispatch(command: Command<R, E>): Result<R, E>
}
