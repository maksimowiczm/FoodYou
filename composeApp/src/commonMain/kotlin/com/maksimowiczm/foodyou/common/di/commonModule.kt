package com.maksimowiczm.foodyou.common.di

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.systemDetails
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    systemDetails()
    factoryOf(::FileKitBlobStorage)
    single { Logger.Companion }.bind<Logger>()
}
