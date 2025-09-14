package com.maksimowiczm.foodyou.app.infrastructure.opensource.database

import com.maksimowiczm.foodyou.app.business.opensource.domain.database.DatabaseDumpService
import com.maksimowiczm.foodyou.app.infrastructure.opensource.room.FoodYouDatabase
import org.koin.core.module.Module
import org.koin.dsl.bind

internal fun Module.databaseModule() {
    factory { RoomDatabaseDumpService(database = get<FoodYouDatabase>()) }
        .bind<DatabaseDumpService>()
}
