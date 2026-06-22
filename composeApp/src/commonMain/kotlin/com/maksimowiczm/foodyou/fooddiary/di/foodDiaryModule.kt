package com.maksimowiczm.foodyou.fooddiary.di

import com.maksimowiczm.foodyou.common.event.di.eventHandlerOf
import com.maksimowiczm.foodyou.fooddiary.application.FoodDataCentralFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryCompositionSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryProductSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryRecipeSynchronizer
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.application.OpenFoodFactsFoodDiarySynchronizer
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCompositionRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.RoomFoodDiaryCompositionRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val foodDiaryModule = module {
    factoryOf(::RoomFoodDiaryCompositionRepository).bind<FoodDiaryCompositionRepository>()
    factoryOf(::FoodDiaryService)

    single { get<FoodDiaryDatabase>().foodDiaryCompositionDao }

    eventHandlerOf(::FoodDiaryCompositionSynchronizer)
    eventHandlerOf(::FoodDiaryProductSynchronizer)
    eventHandlerOf(::FoodDiaryRecipeSynchronizer)
    eventHandlerOf(::OpenFoodFactsFoodDiarySynchronizer)
    eventHandlerOf(::FoodDataCentralFoodDiarySynchronizer)
}
