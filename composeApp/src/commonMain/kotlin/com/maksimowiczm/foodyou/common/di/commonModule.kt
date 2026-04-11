package com.maksimowiczm.foodyou.common.di

import com.maksimowiczm.foodyou.common.domain.blob.BlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.filekit.FileKitBlobStorage
import com.maksimowiczm.foodyou.common.infrastructure.systemDetails
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    systemDetails()
    factoryOf(::FileKitBlobStorage).bind<BlobStorage>()
}
