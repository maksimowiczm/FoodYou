package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.data.database.FoodYouDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.food.data.database.FoodDatabase
import com.maksimowiczm.foodyou.feature.fooddiary.data.FoodDiaryDatabase
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

const val DATABASE_NAME = "open_source_database.db"

expect val databaseDefinition: Module.() -> KoinDefinition<FoodYouDatabase>

val databaseModule = module {
    databaseDefinition().binds(
        arrayOf(
            AboutDatabase::class,
            FoodDatabase::class,
            FoodDiaryDatabase::class
        )
    )
}
