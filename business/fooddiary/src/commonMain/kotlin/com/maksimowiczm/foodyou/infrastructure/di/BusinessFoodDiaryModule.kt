package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateMealWithLastRankCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.DeleteMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.ReorderMealsCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.command.UpdateMealCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.application.query.ObserveMealsQueryHandler
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.query.QueryHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::CreateDiaryEntryCommandHandler) { named("CreateDiaryEntryCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::CreateMealWithLastRankCommandHandler) {
            named("CreateMealWithLastRankCommandHandler")
        }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::DeleteMealCommandHandler) { named("DeleteMealCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::UpdateMealCommandHandler) { named("UpdateMealCommandHandler") }
        .bind<CommandHandler<*, *, *>>()
    factoryOf(::ReorderMealsCommandHandler) { named("ReorderMealsCommandHandler") }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::ObserveMealsQueryHandler) { named("ObserveMealsQueryHandler") }
        .bind<QueryHandler<*, *>>()

    factoryOf(::RoomDiaryEntryDataSource).bind<LocalDiaryEntryDataSource>()
    factoryOf(::RoomMealDataSource).bind<LocalMealDataSource>()
}
