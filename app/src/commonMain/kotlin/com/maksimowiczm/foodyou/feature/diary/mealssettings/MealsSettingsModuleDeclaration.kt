package com.maksimowiczm.foodyou.feature.diary.mealssettings

import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.diary.mealssettings.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.mealssettings.ui.MealsSettingsScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val mealsSettingsModuleDeclaration: ModuleDeclaration = {
    viewModelOf(::MealsSettingsScreenViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
}
