package com.maksimowiczm.foodyou.feature.diary.addfood

import com.maksimowiczm.foodyou.feature.diary.addfood.searchfood.searchFoodModuleDeclaration
import org.koin.dsl.ModuleDeclaration

val addFoodModuleDeclaration: ModuleDeclaration = {
    searchFoodModuleDeclaration()
}
