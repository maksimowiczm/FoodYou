package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.app.application.AppProfileManager
import com.maksimowiczm.foodyou.app.application.ObserveEnergyUnitUseCase
import com.maksimowiczm.foodyou.app.application.ObserveEnergyUnitUseCaseImpl
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.infrastructure.FoodYouConfig
import com.maksimowiczm.foodyou.app.infrastructure.dataStore
import com.maksimowiczm.foodyou.app.infrastructure.room.room
import com.maksimowiczm.foodyou.app.ui.AppViewModel
import com.maksimowiczm.foodyou.common.application.AppConfig
import com.maksimowiczm.foodyou.common.di.applicationCoroutineScope
import com.maksimowiczm.foodyou.common.infrastructure.network.NetworkConfig
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.FoodNameSelectorImpl
import kotlinx.coroutines.CoroutineScope
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.parameter.ParametersHolder
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

class AppModule(
    foodYouConfig: Module.() -> KoinDefinition<out FoodYouConfig>,
    coroutineScope: Scope.(ParametersHolder) -> CoroutineScope,
) {
    val module = module {
        foodYouConfig().binds(arrayOf(AppConfig::class, NetworkConfig::class))
        applicationCoroutineScope(coroutineScope)

        factoryOf(::AppProfileManager)

        factoryOf(::ObserveIsFavoriteFoodUseCase)
        factoryOf(::SetFavoriteFoodUseCase)
        factoryOf(::ObserveEnergyUnitUseCaseImpl).bind<ObserveEnergyUnitUseCase>()

        dataStore()
        room()

        viewModelOf(::AppViewModel)
        factoryOf(::FoodNameSelectorImpl).bind<FoodNameSelector>()
    }
}
