package com.maksimowiczm.foodyou.common.infrastructure

import androidx.room.RoomDatabase

fun <T : RoomDatabase> RoomDatabase.Builder<T>.addHelper(): RoomDatabase.Builder<T> = apply {
    // https://developer.android.com/reference/android/database/sqlite/package-summary
    // Require SQLite version >= 3.35
    if (android.os.Build.VERSION.SDK_INT < 34) {
        openHelperFactory(io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory())
    }
}
