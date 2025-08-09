package com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command

import com.maksimowiczm.foodyou.shared.common.domain.result.Result

interface CommandBus {
    suspend fun <R, E> dispatch(command: Command): Result<R, E>
}

// I think this will crash if returned types are not Unit
suspend fun CommandBus.dispatchIgnoreResult(command: Command) {
    dispatch<Unit, Unit>(command)
}
