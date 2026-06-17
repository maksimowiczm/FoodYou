package com.maksimowiczm.foodyou.app.di

import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.application.ObserveEnergyUnitUseCase
import com.maksimowiczm.foodyou.app.application.ObserveEnergyUnitUseCaseImpl
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.common.application.AppConfig
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

class AppModule(foodYouConfig: Module.() -> KoinDefinition<out FoodYouConfig>) {
    val module = module {
        foodYouConfig().binds(arrayOf(AppConfig::class, NetworkConfig::class))
        factoryOf(::AppProfileManager)

        factoryOf(::ObserveIsFavoriteFoodUseCase)
        factoryOf(::SetFavoriteFoodUseCase)
        factoryOf(::ObserveEnergyUnitUseCaseImpl).bind<ObserveEnergyUnitUseCase>()
    }
}
