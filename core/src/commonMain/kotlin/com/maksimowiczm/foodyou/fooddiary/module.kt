package com.maksimowiczm.foodyou.fooddiary

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.fooddiary.application.FoodDataCentralFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryCompositionSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryMealSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryProductSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryRecipeSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.application.MealPlanFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.OpenFoodFactsFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryMealRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomFoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomFoodDiaryMealRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodDiary() {
    factoryOf(::RoomFoodDiaryCompositionRepository).bind<FoodDiaryCompositionRepository>()
    factoryOf(::RoomFoodDiaryMealRepository).bind<FoodDiaryMealRepository>()
    factoryOf(::FoodDiaryService)

    single { get<FoodDiaryDatabase>().foodDiaryCompositionDao }
    single { get<FoodDiaryDatabase>().foodDiaryMealDao }

    eventHandlerOf(::FoodDiaryCompositionSynchronizer)
    eventHandlerOf(::FoodDiaryMealSynchronizer)
    eventHandlerOf(::MealPlanFoodDiarySynchronizer)
    eventHandlerOf(::FoodDiaryProductSynchronizer)
    eventHandlerOf(::FoodDiaryRecipeSynchronizer)
    eventHandlerOf(::OpenFoodFactsFoodDiarySynchronizer)
    eventHandlerOf(::FoodDataCentralFoodDiarySynchronizer)
}
