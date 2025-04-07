package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.diary.core.coreModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealscard.mealsCardModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.openfoodfactssettings.openFoodFactsSettingsModuleDeclaration
import org.koin.dsl.module

val diaryModule = module {
    coreModuleDeclaration()
    mealsCardModuleDeclaration()
    mealsSettingsModuleDeclaration()
    openFoodFactsSettingsModuleDeclaration()
}
