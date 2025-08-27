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
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.DiaryEntryRepositoryImpl
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.GoalsRepositoryImpl
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.MealRepositoryImpl
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreGoalsDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.datastore.DataStoreMealsPreferencesDataStore
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.room.RoomMealDataSource
import com.maksimowiczm.foodyou.shared.common.application.log.FoodYouLogger
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::RoomDiaryEntryDataSource)
    factoryOf(::RoomMealDataSource)
    factoryOf(::DataStoreGoalsDataSource)
    factoryOf(::DataStoreMealsPreferencesDataStore)

    factory { MealRepositoryImpl(get(), get(), FoodYouLogger) }.bind<MealRepository>()
    factory { DiaryEntryRepositoryImpl(get()) }.bind<DiaryEntryRepository>()
    factory { GoalsRepositoryImpl(get()) }.bind<GoalsRepository>()

    factory { CreateDiaryEntryUseCaseImpl(get(), get(), get(), get(), get(), FoodYouLogger) }
        .bind<CreateDiaryEntryUseCase>()
    factory { ObserveDiaryMealsUseCaseImpl(get(), get(), get()) }.bind<ObserveDiaryMealsUseCase>()
    factory { UnpackDiaryEntryUseCaseImpl(get(), get(), get(), get(), FoodYouLogger) }
        .bind<UnpackDiaryEntryUseCase>()
    factory { UpdateDiaryEntryUseCaseImpl(get(), get(), get(), get(), FoodYouLogger) }
        .bind<UpdateDiaryEntryUseCase>()
}
