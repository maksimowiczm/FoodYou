package com.maksimowiczm.foodyou.search

import com.maksimowiczm.foodyou.search.application.SearchHistoryService
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.search() {
    factoryOf(::SearchHistoryService)
}
