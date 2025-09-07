package com.maksimowiczm.foodyou.app.infrastructure.di

import android.os.Build
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.maksimowiczm.foodyou.app.infrastructure.SystemDetails
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase.Companion.buildDatabase
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope

internal actual fun Scope.database(): FoodYouDatabase =
    Room.databaseBuilder(
            context = androidContext(),
            klass = FoodYouDatabase::class.java,
            name = DATABASE_NAME,
        )
        .apply {
            // https://developer.android.com/reference/android/database/sqlite/package-summary
            // Require SQLite version >= 3.35
            if (Build.VERSION.SDK_INT < 34) {
                openHelperFactory(RequerySQLiteOpenHelperFactory())
            }
        }
        .buildDatabase(mealsCallback = get())

actual fun Scope.createDataStore(): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath {
        androidContext().filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
    }

internal actual val systemDetails: Module.() -> KoinDefinition<out SystemDetails> = {
    singleOf(::SystemDetails)
}
