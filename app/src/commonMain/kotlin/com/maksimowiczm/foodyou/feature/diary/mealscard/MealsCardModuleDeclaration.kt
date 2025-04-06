package com.maksimowiczm.foodyou.feature.diary.mealscard

import com.maksimowiczm.foodyou.feature.diary.mealscard.domain.ObserveMealsUseCase
import com.maksimowiczm.foodyou.feature.diary.mealscard.domain.ObserveMealsUseCaseImpl
import com.maksimowiczm.foodyou.feature.diary.mealscard.ui.MealsCardViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.ModuleDeclaration
import org.koin.dsl.bind

val mealsCardModuleDeclaration: ModuleDeclaration = {
    viewModelOf(::MealsCardViewModel)
    factoryOf(::ObserveMealsUseCaseImpl).bind<ObserveMealsUseCase>()
}
