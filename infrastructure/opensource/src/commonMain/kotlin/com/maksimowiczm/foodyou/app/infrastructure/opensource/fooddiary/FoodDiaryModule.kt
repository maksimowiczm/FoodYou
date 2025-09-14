package com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary

import com.maksimowiczm.foodyou.app.business.shared.di.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.repository.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.repository.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.repository.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.repository.RoomMealRepository
import com.maksimowiczm.foodyou.app.infrastructure.opensource.fooddiary.room.InitializeMealsCallback
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

internal fun Module.foodDiaryModule() {
    factoryOf(::InitializeMealsCallback)

    userPreferencesRepositoryOf(::DataStoreMealsPreferencesRepository)
    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()
    factoryOf(::RoomMealRepository).bind<MealRepository>()
}
