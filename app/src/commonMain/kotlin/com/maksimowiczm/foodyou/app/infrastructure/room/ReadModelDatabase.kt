package com.maksimowiczm.foodyou.app.infrastructure.room

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.common.infrastructure.room.FoodCompositionComponentIdentityConverter
import com.maksimowiczm.foodyou.common.infrastructure.room.UuidConverter
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
        ],
    views = [UserRecipeFlattenedCompositionView::class],
    version = ReadModelDatabase.VERSION,
    exportSchema = false,
)
@TypeConverters(
    UuidConverter::class,
    QuantityTypeConverter::class,
    FoodCompositionComponentIdentityConverter::class,
)
@ConstructedBy(ReadModelDatabaseConstructor::class)
internal abstract class ReadModelDatabase :
    RoomDatabase(), SearchDatabase, UserRecipeDatabase, FoodDiaryDatabase {
    companion object {
        const val VERSION = 1

        fun Builder<ReadModelDatabase>.buildDatabase(): ReadModelDatabase = build()
    }
}

@Suppress("KotlinNoActualForExpect")
internal expect object ReadModelDatabaseConstructor : RoomDatabaseConstructor<ReadModelDatabase> {
    override fun initialize(): ReadModelDatabase
}
