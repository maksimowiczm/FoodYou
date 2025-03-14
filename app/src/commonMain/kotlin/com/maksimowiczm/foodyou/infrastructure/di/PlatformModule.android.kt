package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.AndroidStringFormatRepository
import com.maksimowiczm.foodyou.data.AndroidSystemInfoRepository
import com.maksimowiczm.foodyou.data.StringFormatRepository
import com.maksimowiczm.foodyou.data.SystemInfoRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val platformModule = module {
    factoryOf(::AndroidSystemInfoRepository).bind<SystemInfoRepository>()
    factoryOf(::AndroidStringFormatRepository).bind<StringFormatRepository>()
}
