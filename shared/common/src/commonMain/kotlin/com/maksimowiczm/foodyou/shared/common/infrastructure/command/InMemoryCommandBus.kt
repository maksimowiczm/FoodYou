package com.maksimowiczm.foodyou.shared.common.infrastructure.command

import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.Command
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.result.Result
import kotlin.reflect.KClass

internal class InMemoryCommandBus(commandHandlers: List<CommandHandler<Command, *, *>>) :
    CommandBus {

    private val commandHandlers: Map<KClass<Command>, CommandHandler<Command, *, *>> =
        commandHandlers.associateBy { it.commandType }

    override suspend fun <R, E> dispatch(command: Command): Result<R, E> {
        @Suppress("UNCHECKED_CAST")
        val handler =
            commandHandlers[command::class] as? CommandHandler<Command, R, E>
                ?: error("No handler found for command: ${command::class.simpleName}")

        return handler.handle(command)
    }
}
