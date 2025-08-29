package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.settings.infrastructure.SystemDetails
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

internal actual val systemDetails: Module.() -> KoinDefinition<out SystemDetails> = {
    singleOf(::SystemDetails)
}
