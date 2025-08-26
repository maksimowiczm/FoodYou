package com.maksimowiczm.foodyou.business.shared.infrastructure.command

import com.maksimowiczm.foodyou.business.shared.application.command.Command
import com.maksimowiczm.foodyou.business.shared.application.command.CommandBus
import com.maksimowiczm.foodyou.business.shared.application.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.result.Result
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named

internal class KoinCommandBus() : CommandBus, KoinComponent {
    override suspend fun <R, E> dispatch(command: Command<R, E>): Result<R, E> {
        val kclass = command::class.qualifiedName!!
        val handler = get(named(kclass)) as CommandHandler<Command<R, E>, R, E>
        return handler.handle(command)
    }
}
