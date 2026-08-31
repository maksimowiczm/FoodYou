package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room3.*
import com.maksimowiczm.foodyou.common.infrastructure.room.DateConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSnapshotConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodSnapshotIdConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter
import com.maksimowiczm.foodyou.features.home.integration.HomeDao
import com.maksimowiczm.foodyou.features.home.integration.HomeEntryEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryDatabase
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryEntryMealEntity
import com.maksimowiczm.foodyou.fooddiary.infrastructure.room.FoodDiaryEntryReferenceEntity
import com.maksimowiczm.foodyou.search.infrastructure.QuantityTypeConverter
import com.maksimowiczm.foodyou.search.infrastructure.SearchDatabase
import com.maksimowiczm.foodyou.search.infrastructure.SearchEntity
import com.maksimowiczm.foodyou.search.infrastructure.SearchEntityFts
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.UserRecipeCompositionReferenceEntity
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.UserRecipeDatabase
import com.maksimowiczm.foodyou.userrecipe.infrastructure.room.UserRecipeFlattenedCompositionView

@Database(
    entities =
        [
            SearchEntityFts::class,
            SearchEntity::class,
            UserRecipeCompositionReferenceEntity::class,
            FoodDiaryEntryReferenceEntity::class,
            FoodDiaryEntryMealEntity::class,
            HomeEntryEntity::class,
        ],
    views = [UserRecipeFlattenedCompositionView::class],
    version = ReadModelDatabase.VERSION,
    exportSchema = false,
)
@ColumnTypeConverters(
    UuidConverter::class,
    QuantityTypeConverter::class,
    FoodSnapshotIdConverter::class,
    DateConverters::class,
    FoodSnapshotConverter::class,
)
@ConstructedBy(ReadModelDatabaseConstructor::class)
internal abstract class ReadModelDatabase :
    RoomDatabase(), SearchDatabase, UserRecipeDatabase, FoodDiaryDatabase {
    abstract val homeDao: HomeDao

    companion object {
        const val VERSION = 1

        fun Builder<ReadModelDatabase>.buildDatabase(): ReadModelDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object ReadModelDatabaseConstructor : RoomDatabaseConstructor<ReadModelDatabase> {
    override fun initialize(): ReadModelDatabase
}
