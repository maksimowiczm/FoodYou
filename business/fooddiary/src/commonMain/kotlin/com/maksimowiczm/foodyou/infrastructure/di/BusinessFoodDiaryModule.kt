package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateMealWithLastRankCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.ReorderMealsCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UnpackDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealsPreferencesCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateWeeklyGoalsCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDailyGoalsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryEntryQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsPreferencesQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveWeeklyGoalsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.datastore.DataStoreGoalsDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.datastore.DataStoreMealsPreferencesDataStore
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    commandHandlerOf(::CreateDiaryEntryCommandHandler)
    commandHandlerOf(::CreateMealWithLastRankCommandHandler)
    commandHandlerOf(::DeleteDiaryEntryCommandHandler)
    commandHandlerOf(::DeleteMealCommandHandler)
    commandHandlerOf(::UpdateMealCommandHandler)
    commandHandlerOf(::ReorderMealsCommandHandler)
    commandHandlerOf(::UpdateWeeklyGoalsCommandHandler)
    commandHandlerOf(::UpdateMealsPreferencesCommandHandler)
    commandHandlerOf(::UpdateDiaryEntryCommandHandler)
    commandHandlerOf(::UnpackDiaryEntryCommandHandler)

    queryHandlerOf(::ObserveMealsQueryHandler)
    queryHandlerOf(::ObserveWeeklyGoalsQueryHandler)
    queryHandlerOf(::ObserveDiaryMealsQueryHandler)
    queryHandlerOf(::ObserveMealsPreferencesQueryHandler)
    queryHandlerOf(::ObserveDailyGoalsQueryHandler)
    queryHandlerOf(::ObserveMealQueryHandler)
    queryHandlerOf(::ObserveDiaryEntryQueryHandler)

    factoryOf(::RoomDiaryEntryDataSource).bind<LocalDiaryEntryDataSource>()
    factoryOf(::RoomMealDataSource).bind<LocalMealDataSource>()
    factoryOf(::DataStoreGoalsDataSource).bind<LocalGoalsDataSource>()
    factoryOf(::DataStoreMealsPreferencesDataStore).bind<LocalMealsPreferencesDataSource>()
}
