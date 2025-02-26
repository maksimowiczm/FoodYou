package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.home.mealscard.ui.DiaryDayMealViewModel
import com.maksimowiczm.foodyou.feature.home.mealscard.ui.MealsCardViewModel
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.AddFoodViewModel
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion.CreatePortionViewModel
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.portion.UpdatePortionViewModel
import com.maksimowiczm.foodyou.feature.legacy.addfood.ui.search.SearchViewModel
import com.maksimowiczm.foodyou.feature.legacy.camera.ui.CameraBarcodeScannerViewModel
import com.maksimowiczm.foodyou.feature.legacy.product.ui.CreateProductViewModel
import com.maksimowiczm.foodyou.feature.legacy.product.ui.update.UpdateProductViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

// This is a module that provides dependencies for the legacy features. Going to be removed in the
// near future.
val legacyModule = module {
    // Legacy Add Food Feature
    viewModelOf(::AddFoodViewModel)
    viewModelOf(::SearchViewModel)

    // Legacy Portion Feature
    viewModelOf(::CreatePortionViewModel)
    viewModelOf(::UpdatePortionViewModel)

    // Legacy Camera Feature
    viewModelOf(::CameraBarcodeScannerViewModel)

    // Legacy Diary Feature
    viewModelOf(::MealsCardViewModel)
    viewModelOf(::DiaryDayMealViewModel)

    // Legacy Product Feature
    viewModelOf(::CreateProductViewModel)
    viewModelOf(::UpdateProductViewModel)
}
