package com.maksimowiczm.foodyou.infrastructure.di

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.FoodYouDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.createDatabaseBuilder(): RoomDatabase.Builder<FoodYouDatabase> =
    Room.databaseBuilder(
            context = androidContext(),
            klass = FoodYouDatabase::class.java,
            name = DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.28
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }

actual fun Scope.createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
    }
