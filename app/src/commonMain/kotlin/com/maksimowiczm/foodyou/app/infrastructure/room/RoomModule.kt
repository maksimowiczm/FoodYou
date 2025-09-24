package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.common.domain.database.TransactionProvider
import com.maksimowiczm.foodyou.food.infrastructure.room.FoodDatabase
import com.maksimowiczm.foodyou.food.search.infrastructure.room.FoodSearchDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.sponsorship.infrastructure.room.SponsorshipDatabase
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.binds

internal const val DATABASE_NAME = "open_source_database.db"

internal expect fun Scope.database(): FoodYouDatabase

private val Scope.database: FoodYouDatabase
    get() = get<FoodYouDatabase>()

fun Module.roomModule() {
    single<FoodYouDatabase> { database() }
        .binds(
            arrayOf(
                RoomDatabase::class,
                TransactionProvider::class,
                FoodDatabase::class,
                FoodSearchDatabase::class,
                FoodDiaryDatabase::class,
                SponsorshipDatabase::class,
            )
        )
}
