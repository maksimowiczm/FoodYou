package com.maksimowiczm.foodyou.common.di

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryParser
import com.maksimowiczm.foodyou.common.infrastructure.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.systemDetails
import kotlin.time.Clock
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

val commonModule = module {
    systemDetails()
    factoryOf(::FileKitBlobStorage).binds(arrayOf(BlobStorage::class, BlobResolver::class))
    single { Logger.Companion }.bind<Logger>()
    single { Clock.System }.bind<Clock>()
    factoryOf(::SearchQueryParser)
}
