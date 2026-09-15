package com.maksimowiczm.foodyou.common

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.BlobResolver
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryParser
import com.maksimowiczm.foodyou.common.infrastructure.FileKitBlobStorage
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.binds

internal fun Module.common() {
    factoryOf(::FileKitBlobStorage).binds(arrayOf(BlobStorage::class, BlobResolver::class))
    single { Logger.Companion }.bind<Logger>()
    single(named(SearchQuery.Barcode::class.qualifiedName!!)) { SearchQuery.Barcode.recognizer }
    factory { SearchQueryParser(getAll()) }
}
