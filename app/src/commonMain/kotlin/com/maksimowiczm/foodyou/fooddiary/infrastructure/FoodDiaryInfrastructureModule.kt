package com.maksimowiczm.foodyou.fooddiary.infrastructure

import com.maksimowiczm.foodyou.common.infrastructure.koin.userPreferencesRepositoryOf
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.service.LocalizedMealsProvider
import com.maksimowiczm.foodyou.fooddiary.infrastructure.compose.ComposeLocalizedMealsProvider
import com.maksimowiczm.foodyou.fooddiary.infrastructure.repository.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.repository.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.repository.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.repository.RoomMealRepository
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.InitializeMealsCallback
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind

internal fun Module.foodDiaryInfrastructureModule() {
    factoryOf(::ComposeLocalizedMealsProvider).bind<LocalizedMealsProvider>()

    userPreferencesRepositoryOf(::DataStoreMealsPreferencesRepository)
    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()
    factoryOf(::RoomMealRepository).bind<MealRepository>()

    factoryOf(::InitializeMealsCallback)

    factory { database.mealDao }
    factory { database.manualDiaryEntryDao }
    factory { database.measurementDao }
}

private val Scope.database: FoodDiaryDatabase
    get() = get()
