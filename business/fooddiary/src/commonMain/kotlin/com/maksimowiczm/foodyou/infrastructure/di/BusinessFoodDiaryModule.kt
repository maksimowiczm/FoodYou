package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommandHandler
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.LocalMealDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomDiaryEntryDataSource
import com.maksimowiczm.foodyou.business.fooddiary.infrastructure.persistence.room.RoomMealDataSource
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandHandler
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.named
import org.koin.dsl.bind
import org.koin.dsl.module

val businessFoodDiaryModule = module {
    factoryOf(::CreateDiaryEntryCommandHandler) { named("CreateDiaryEntryCommandHandler") }
        .bind<CommandHandler<*, *, *>>()

    factoryOf(::RoomDiaryEntryDataSource).bind<LocalDiaryEntryDataSource>()
    factoryOf(::RoomMealDataSource).bind<LocalMealDataSource>()
}
