package com.maksimowiczm.foodyou.fooddiary

import com.maksimowiczm.foodyou.fooddiary.domain.foodDiaryDomainModule
import com.maksimowiczm.foodyou.fooddiary.infrastructure.foodDiaryInfrastructureModule
import org.koin.dsl.module

val foodDiaryModule = module {
    foodDiaryDomainModule()
    foodDiaryInfrastructureModule()
}
