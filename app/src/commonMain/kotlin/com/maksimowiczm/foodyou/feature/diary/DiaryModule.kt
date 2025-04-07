package com.maksimowiczm.foodyou.feature.diary

import com.maksimowiczm.foodyou.feature.diary.addfood.addFoodModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.core.coreModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealscard.mealsCardModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.mealssettings.mealsSettingsModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.openfoodfacts.openFoodFactsSettingsModuleDeclaration
import org.koin.dsl.module

val diaryModule = module {
    addFoodModuleDeclaration()
    coreModuleDeclaration()
    mealsCardModuleDeclaration()
    mealsSettingsModuleDeclaration()
    openFoodFactsSettingsModuleDeclaration()
}
