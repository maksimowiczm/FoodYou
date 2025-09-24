package com.maksimowiczm.foodyou.food.search

import com.maksimowiczm.foodyou.food.search.domain.foodSearchDomainModule
import com.maksimowiczm.foodyou.food.search.infrastructure.foodSearchInfrastructureModule
import org.koin.dsl.module

val foodSearchModule = module {
    foodSearchDomainModule()
    foodSearchInfrastructureModule()
}
