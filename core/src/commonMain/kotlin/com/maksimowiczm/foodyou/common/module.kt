package com.maksimowiczm.foodyou.common

import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.common.domain.BlobStorage
import com.maksimowiczm.foodyou.common.domain.search.SearchQuery
import com.maksimowiczm.foodyou.common.domain.search.SearchQueryParser
import com.maksimowiczm.foodyou.common.infrastructure.FileKitBlobStorage
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind

internal fun Module.common() {
    factoryOf(::FileKitBlobStorage).bind<BlobStorage>()
    single { Logger.Companion }.bind<Logger>()
    single(named(SearchQuery.Barcode::class.qualifiedName!!)) { SearchQuery.Barcode.recognizer }
    factory { SearchQueryParser(getAll()) }
}
