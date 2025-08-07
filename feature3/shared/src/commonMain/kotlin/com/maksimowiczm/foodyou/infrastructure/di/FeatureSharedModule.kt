package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCase
import com.maksimowiczm.foodyou.feature.shared.usecase.ObserveSettingsUseCaseImpl
import com.maksimowiczm.foodyou.feature.shared.usecase.UpdateSettingsUseCase
import com.maksimowiczm.foodyou.feature.shared.usecase.UpdateSettingsUseCaseImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val featureSharedModule = module {
    factoryOf(::ObserveSettingsUseCaseImpl).bind<ObserveSettingsUseCase>()
    factoryOf(::UpdateSettingsUseCaseImpl).bind<UpdateSettingsUseCase>()
}
