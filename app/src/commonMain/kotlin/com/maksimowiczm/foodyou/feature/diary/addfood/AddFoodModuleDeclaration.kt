package com.maksimowiczm.foodyou.feature.diary.addfood

import com.maksimowiczm.foodyou.feature.diary.addfood.meal.mealModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.addfood.measurement.measurementModuleDeclaration
import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.searchFoodModuleDeclaration
import org.koin.dsl.ModuleDeclaration

val addFoodModuleDeclaration: ModuleDeclaration = {
    mealModuleDeclaration()
    measurementModuleDeclaration()
    searchFoodModuleDeclaration()
}
