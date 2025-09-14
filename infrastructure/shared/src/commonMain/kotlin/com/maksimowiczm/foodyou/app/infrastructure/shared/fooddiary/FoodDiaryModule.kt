package com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary

import com.maksimowiczm.foodyou.app.infrastructure.shared.fooddiary.compose.ComposeLocalizedMealsProvider
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodDiaryModule() {
    factoryOf(::ComposeLocalizedMealsProvider).bind<LocalizedMealsProvider>()
}
