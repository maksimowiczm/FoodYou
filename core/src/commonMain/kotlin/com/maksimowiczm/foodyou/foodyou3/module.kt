package com.maksimowiczm.foodyou.foodyou3

import com.maksimowiczm.foodyou.foodyou3.application.FoodYou3DataScopeFactory
import com.maksimowiczm.foodyou.foodyou3.infrastructure.SqliteFoodYou3DataScopeFactory
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodYou3() {
    factoryOf(::SqliteFoodYou3DataScopeFactory).bind<FoodYou3DataScopeFactory>()
}
