package com.maksimowiczm.foodyou.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.maksimowiczm.foodyou.feature.about.data.database.AboutDatabase
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorMethodConverter
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship

@Database(
    entities = [
        Sponsorship::class
    ],
    version = FoodYouDatabase.VERSION,
    exportSchema = true
)
@TypeConverters(
    SponsorMethodConverter::class
)
abstract class FoodYouDatabase :
    RoomDatabase(),
    AboutDatabase {

    companion object {
        const val VERSION = 1

        fun Builder<FoodYouDatabase>.buildDatabase(): FoodYouDatabase = build()
    }
}
