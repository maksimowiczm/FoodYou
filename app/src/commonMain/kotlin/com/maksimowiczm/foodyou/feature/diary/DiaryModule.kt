package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepository
import com.maksimowiczm.foodyou.feature.diary.data.meal.MealRepositoryImpl
import com.maksimowiczm.foodyou.feature.diary.mealscard.mealsCardModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsModuleDeclaration
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val diaryModule = module {
    factoryOf(::MealRepositoryImpl).bind<MealRepository>()

    mealsSettingsModuleDeclaration()
    mealsCardModuleDeclaration()
}
