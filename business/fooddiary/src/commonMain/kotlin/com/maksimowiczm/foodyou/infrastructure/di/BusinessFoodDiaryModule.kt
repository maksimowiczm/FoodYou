package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.CreateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.CreateFoodDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.ObserveDiaryMealsUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.UnpackFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.UnpackFoodDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.UpdateFoodDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.UpdateFoodDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.domain.FoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.GoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.ManualDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferencesRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreGoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomFoodDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomManualDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomMealRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::RoomMealRepository).bind<MealRepository>()
    factoryOf(::RoomFoodDiaryEntryRepository).bind<FoodDiaryEntryRepository>()
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()
    factoryOf(::DataStoreMealsPreferencesRepository).bind<MealsPreferencesRepository>()
    factoryOf(::RoomManualDiaryEntryRepository).bind<ManualDiaryEntryRepository>()

    factoryOf(::CreateFoodDiaryEntryUseCaseImpl).bind<CreateFoodDiaryEntryUseCase>()
    factoryOf(::ObserveDiaryMealsUseCaseImpl).bind<ObserveDiaryMealsUseCase>()
    factoryOf(::UnpackFoodDiaryEntryUseCaseImpl).bind<UnpackFoodDiaryEntryUseCase>()
    factoryOf(::UpdateFoodDiaryEntryUseCaseImpl).bind<UpdateFoodDiaryEntryUseCase>()
}
