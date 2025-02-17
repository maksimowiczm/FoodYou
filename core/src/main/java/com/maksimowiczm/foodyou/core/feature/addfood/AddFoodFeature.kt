package com.maksimowiczm.foodyou.core.feature.addfood

import com.maksimowiczm.foodyou.core.feature.Feature
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.core.feature.addfood.data.AddFoodRepositoryImpl
import com.maksimowiczm.foodyou.core.feature.addfood.ui.AddFoodViewModel
import com.maksimowiczm.foodyou.core.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.core.feature.addfood.ui.search.SearchViewModel
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

private val addFoodModule = module {
    viewModelOf(::AddFoodViewModel)
    viewModelOf(::PortionViewModel)
    viewModelOf(::SearchViewModel)

    factory {
        AddFoodRepositoryImpl(
            addFoodDatabase = get(),
            productDatabase = get(),
            productRemoteMediatorFactory = get()
        )
    }.bind<AddFoodRepository>()
}

/**
 * AddFoodFeature is a feature that provides functionality for adding food to the diary. Should be
 * used with [com.maksimowiczm.foodyou.core.feature.addfood.navigation.addFoodGraph].
 */
object AddFoodFeature : Feature.Koin {
    override fun KoinApplication.setup() {
        modules(addFoodModule)
    }
}
