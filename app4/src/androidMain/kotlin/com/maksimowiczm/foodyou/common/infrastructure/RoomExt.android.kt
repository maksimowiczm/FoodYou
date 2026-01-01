package com.maksimowiczm.foodyou.common.infrastructure

import androidx.room.Room
import androidx.room.RoomDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

fun <T : RoomDatabase> RoomDatabase.Builder<T>.addHelper(): RoomDatabase.Builder<T> = apply {
    // https://developer.android.com/reference/android/database/sqlite/package-summary
    // Require SQLite version >= 3.35
    if (android.os.Build.VERSION.SDK_INT < 34) {
        openHelperFactory(io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory())
    }
}

actual inline fun <reified T : RoomDatabase> Scope.databaseBuilder(
    name: String
): RoomDatabase.Builder<T> =
    Room.databaseBuilder(context = androidContext(), klass = T::class.java, name = name).addHelper()
