package com.maksimowiczm.foodyou.infrastructure.di

import android.os.Build
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase
import com.maksimowiczm.foodyou.core.database.FoodYouDatabase.Companion.buildDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module

actual val databaseDefinition: Module.() -> KoinDefinition<FoodYouDatabase> = {
    single {
        val builder: RoomDatabase.Builder<FoodYouDatabase> =
            Room.databaseBuilder(
                context = androidContext(),
                klass = FoodYouDatabase::class.java,
                name = DATABASE_NAME
            )

        // https://developer.android.com/reference/android/database/sqlite/package-summary
        // Require SQLite version >= 3.28
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            builder.openHelperFactory(RequerySQLiteOpenHelperFactory())
        }

        builder.buildDatabase()
    }
}
