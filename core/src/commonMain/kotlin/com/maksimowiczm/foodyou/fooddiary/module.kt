package com.maksimowiczm.foodyou.fooddiary

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.fooddiary.application.FoodDataCentralFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryMealSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryProductSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryRecipeSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiarySnapshotSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.MealPlanFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.MealPlanQuickFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.OpenFoodFactsFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.QuickFoodDiaryMealSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.QuickFoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomFoodDiaryEntryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomFoodDiaryEntryMealRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomQuickFoodDiaryEntryMealRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodDiary() {
    factoryOf(::RoomFoodDiaryEntryCompositionRepository).bind<FoodDiaryEntryCompositionRepository>()
    factoryOf(::RoomFoodDiaryEntryMealRepository).bind<FoodDiaryEntryMealRepository>()
    factoryOf(::FoodDiaryService)

    single { get<FoodDiaryDatabase>().foodDiaryEntryCompositionDao }
    single { get<FoodDiaryDatabase>().foodDiaryEntryMealDao }
    single { get<FoodDiaryDatabase>().quickFoodDiaryEntryMealDao }

    factoryOf(::RoomQuickFoodDiaryEntryMealRepository).bind<QuickFoodDiaryEntryMealRepository>()
    factoryOf(::QuickFoodDiaryService)

    eventHandlerOf(::FoodDiarySnapshotSynchronizer)
    eventHandlerOf(::FoodDiaryMealSynchronizer)
    eventHandlerOf(::MealPlanFoodDiarySynchronizer)
    eventHandlerOf(::FoodDiaryProductSynchronizer)
    eventHandlerOf(::FoodDiaryRecipeSynchronizer)
    eventHandlerOf(::OpenFoodFactsFoodDiarySynchronizer)
    eventHandlerOf(::FoodDataCentralFoodDiarySynchronizer)

    eventHandlerOf(::QuickFoodDiaryMealSynchronizer)
    eventHandlerOf(::MealPlanQuickFoodDiarySynchronizer)
}
