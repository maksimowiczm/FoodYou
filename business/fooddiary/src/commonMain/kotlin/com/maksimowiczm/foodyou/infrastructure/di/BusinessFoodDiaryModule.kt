package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateMealWithLastRankCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateMealWithLastRankCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteMealCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.ReorderMealsCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.ReorderMealsCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealsPreferencesCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealsPreferencesCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateWeeklyGoalsCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateWeeklyGoalsCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDailyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDailyGoalsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveDiaryMealsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsPreferencesQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsPreferencesQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveWeeklyGoalsQuery
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveWeeklyGoalsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalGoalsDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.LocalMealsPreferencesDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.datastore.DataStoreGoalsDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.preferences.datastore.DataStoreMealsPreferencesDataStore
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::CreateDiaryEntryCommandHandler) {
            named(CreateDiaryEntryCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::CreateMealWithLastRankCommandHandler) {
            named(CreateMealWithLastRankCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::DeleteMealCommandHandler) { named(DeleteMealCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateMealCommandHandler) { named(UpdateMealCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ReorderMealsCommandHandler) { named(ReorderMealsCommand::class.qualifiedName!!) }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateWeeklyGoalsCommandHandler) {
            named(UpdateWeeklyGoalsCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateMealsPreferencesCommandHandler) {
            named(UpdateMealsPreferencesCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::DeleteDiaryEntryCommandHandler) {
            named(DeleteDiaryEntryCommand::class.qualifiedName!!)
        }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::ObserveMealsQueryHandler) { named(ObserveMealsQuery::class.qualifiedName!!) }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveWeeklyGoalsQueryHandler) {
            named(ObserveWeeklyGoalsQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveDiaryMealsQueryHandler) {
            named(ObserveDiaryMealsQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveMealsPreferencesQueryHandler) {
            named(ObserveMealsPreferencesQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveDailyGoalsQueryHandler) {
            named(ObserveDailyGoalsQuery::class.qualifiedName!!)
        }
        .bind<QueryHandler<*, *>>()
    factoryOf(::ObserveMealQueryHandler) { named(ObserveMealQuery::class.qualifiedName!!) }
        .bind<QueryHandler<*, *>>()

    factoryOf(::RoomDiaryEntryDataSource).bind<LocalDiaryEntryDataSource>()
    factoryOf(::RoomMealDataSource).bind<LocalMealDataSource>()
    factoryOf(::DataStoreGoalsDataSource).bind<LocalGoalsDataSource>()
    factoryOf(::DataStoreMealsPreferencesDataStore).bind<LocalMealsPreferencesDataSource>()
}
