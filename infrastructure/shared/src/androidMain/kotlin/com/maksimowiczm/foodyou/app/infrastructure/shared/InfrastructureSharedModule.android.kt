package com.maksimowiczm.foodyou.app.infrastructure.shared

import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

internal actual val systemDetails: Module.() -> KoinDefinition<out SystemDetails> = {
    singleOf(::SystemDetails)
}
