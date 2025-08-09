package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.feature.food.shared.usecase.ObserveFoodUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureFoodSharedModule = module {
    factoryOf(::ObserveFoodUseCaseImpl).bind<ObserveFoodUseCase>()
}
