package com.maksimowiczm.foodyou.app.infrastructure.shared.csv

import com.maksimowiczm.foodyou.app.business.shared.domain.csv.CsvParser
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.csvModule() {
    factoryOf(::VibeCsvParser).bind<CsvParser>()
}
