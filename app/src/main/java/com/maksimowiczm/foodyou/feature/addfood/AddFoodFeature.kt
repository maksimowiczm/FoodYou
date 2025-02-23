package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.Feature
import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.AddFoodViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.portion.PortionViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchViewModel
import org.koin.core.KoinApplication
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * AddFoodFeature is a feature that provides functionality for adding food to the diary. Should be
 * used with [com.maksimowiczm.foodyou.feature.addfood.navigation.addFoodGraph].
 */
abstract class AddFoodFeature(
    private val addFoodRepository: Module.() -> KoinDefinition<AddFoodRepository>
) : Feature.Koin {
    final override fun KoinApplication.setup() {
        modules(
            module {
                viewModelOf(::AddFoodViewModel)
                viewModelOf(::PortionViewModel)
                viewModelOf(::SearchViewModel)

                addFoodRepository().bind()
            }
        )
    }
}
