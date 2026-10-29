package com.maksimowiczm.foodyou.swissfoodcomposition

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionRepository
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.SwissFoodCompositionRepositoryImpl
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase.Companion.buildDatabase
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.path
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

private const val SWISS_FOOD_COMPOSITION_DATABASE_NAME = "SwissFoodCompositionDatabase.db"

internal fun Module.swissFoodComposition() {
    single {
        val dbFile = FileKit.databasesDir / SWISS_FOOD_COMPOSITION_DATABASE_NAME
        Room.databaseBuilder<SwissFoodCompositionDatabase>(dbFile.path)
            .setDriver(BundledSQLiteDriver())
            .buildDatabase()
    }
    factory { get<SwissFoodCompositionDatabase>().swissFoodCompositionDao }

    factoryOf(::SwissFoodCompositionRepositoryImpl).bind<SwissFoodCompositionRepository>()
}
