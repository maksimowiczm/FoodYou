package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command

import com.maksimowiczm.foodyou.shared.common.domain.result.Result

interface CommandBus {
    suspend fun <R, E> dispatch(command: Command<R, E>): Result<R, E>
}
