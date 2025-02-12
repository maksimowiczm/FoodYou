package com.maksimowiczm.foodyou.core.infrastructure.database

import android.content.Context
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.core.feature.addfood.database.InitializeMealsCallback
import com.maksimowiczm.foodyou.core.infrastructure.database.FoodYouDatabase.Companion.buildDatabase

fun RoomDatabase.Builder<FoodYouDatabase>.buildDatabase(context: Context): FoodYouDatabase {
    addCallback(InitializeMealsCallback(context))

    return buildDatabase()
}
