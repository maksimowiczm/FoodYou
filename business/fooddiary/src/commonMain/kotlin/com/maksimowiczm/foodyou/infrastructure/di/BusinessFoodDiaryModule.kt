package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.CreateDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.CreateDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.ObserveDiaryMealsUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.ObserveDiaryMealsUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.UnpackDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.UnpackDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.application.UpdateDiaryEntryUseCase
import com.maksimowiczm.foodyou.business.fooddiary.application.UpdateDiaryEntryUseCaseImpl
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.GoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealRepository
import com.maksimowiczm.foodyou.business.fooddiary.domain.MealsPreferencesRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreGoalsRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreMealsPreferencesRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomDiaryEntryRepository
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomMealRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::RoomMealRepository).bind<MealRepository>()
    factoryOf(::RoomDiaryEntryRepository).bind<DiaryEntryRepository>()
    factoryOf(::DataStoreGoalsRepository).bind<GoalsRepository>()
    factoryOf(::DataStoreMealsPreferencesRepository).bind<MealsPreferencesRepository>()

    factoryOf(::CreateDiaryEntryUseCaseImpl).bind<CreateDiaryEntryUseCase>()
    factoryOf(::ObserveDiaryMealsUseCaseImpl).bind<ObserveDiaryMealsUseCase>()
    factoryOf(::UnpackDiaryEntryUseCaseImpl).bind<UnpackDiaryEntryUseCase>()
    factoryOf(::UpdateDiaryEntryUseCaseImpl).bind<UpdateDiaryEntryUseCase>()
}
