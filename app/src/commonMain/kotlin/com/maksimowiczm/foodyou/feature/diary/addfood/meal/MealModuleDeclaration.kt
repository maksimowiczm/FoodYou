package com.maksimowiczm.foodyou.feature.diary.addfood.meal

import com.maksimowiczm.foodyou.feature.diary.addfood.meal.domain.ObserveMealUseCase
import com.maksimowiczm.foodyou.feature.diary.addfood.meal.domain.ObserveMealUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.addfood.meal.ui.MealScreenViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val mealModuleDeclaration: ModuleDeclaration = {
    viewModelOf(::MealScreenViewModel)
    factoryOf(::ObserveMealUseCaseImpl).bind<ObserveMealUseCase>()
}
