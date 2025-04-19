package com.maksimowiczm.foodyou.feature.addfood

import com.maksimowiczm.foodyou.feature.addfood.data.AddFoodRepository
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.CreateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.measurement.UpdateMeasurementScreenViewModel
import com.maksimowiczm.foodyou.feature.addfood.ui.search.SearchFoodViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val addFoodModule = module {
    viewModelOf(::SearchFoodViewModel)
    viewModelOf(::CreateMeasurementScreenViewModel)
    viewModelOf(::UpdateMeasurementScreenViewModel)

    factory {
        AddFoodRepository(
            searchLocalDataSource = get(),
            foodLocalDataSource = get()
        )
    }
}
