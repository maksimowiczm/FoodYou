package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferences
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.business.fooddiary.domain.ObserveDiaryMealsUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreGoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomMealRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.fooddiary.domain.repository.MealRepository
import com.maksimowiczm.foodyou.goals.domain.repository.GoalsRepository
import com.maksimowiczm.foodyou.shared.userpreferences.UserPreferencesRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val mealsPreferencesQualifier = named(MealsPreferences::class.qualifiedName!!)

val businessFoodDiaryModule = module {
    factoryOf(::RoomMealRepository).bind<MealRepository>()
    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()
    factoryOf(::DataStoreMealsPreferencesRepository) { qualifier = mealsPreferencesQualifier }
        .bind<UserPreferencesRepository<MealsPreferences>>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()

    factoryOf(::ObserveDiaryMealsUseCaseImpl).bind<ObserveDiaryMealsUseCase>()
}
