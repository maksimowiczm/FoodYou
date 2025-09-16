package com.maksimowiczm.foodyou.app.business.opensource.di

import com.maksimowiczm.foodyou.food.domain.usecase.CreateProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.CreateRecipeUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.DeleteFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.DownloadProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveFoodUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.ObserveMeasurementSuggestionsUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.UpdateProductUseCase
import com.maksimowiczm.foodyou.food.domain.usecase.UpdateRecipeUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.CreateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.fooddiary.domain.usecase.UpdateFoodDiaryEntryUseCase
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

internal fun Module.includeCoreUseCases() {
    // Food
    factoryOf(::CreateProductUseCase)
    factoryOf(::CreateRecipeUseCase)
    factoryOf(::DeleteFoodUseCase)
    factoryOf(::DownloadProductUseCase)
    factoryOf(::ObserveFoodUseCase)
    factoryOf(::ObserveMeasurementSuggestionsUseCase)
    factoryOf(::UpdateProductUseCase)
    factoryOf(::UpdateRecipeUseCase)

    // Food Diary
    factoryOf(::CreateFoodDiaryEntryUseCase)
    factoryOf(::UnpackFoodDiaryEntryUseCase)
    factoryOf(::UpdateFoodDiaryEntryUseCase)
}
