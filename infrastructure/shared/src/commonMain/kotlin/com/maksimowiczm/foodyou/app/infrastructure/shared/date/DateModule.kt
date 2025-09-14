package com.maksimowiczm.foodyou.app.infrastructure.shared.date

import com.maksimowiczm.foodyou.shared.domain.date.DateProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

internal fun Module.dateModule() {
    singleOf(::DateProviderImpl).bind<DateProvider>()
}
