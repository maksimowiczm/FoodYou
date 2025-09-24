package com.maksimowiczm.foodyou.common.infrastructure.csv

import com.maksimowiczm.foodyou.common.csv.CsvParser
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

fun Module.csvModule() {
    factoryOf(::VibeCsvParser).bind<CsvParser>()
}
