package com.maksimowiczm.foodyou.common.extension

import androidx.room3.*
import org.koin.core.scope.Scope

expect inline fun <reified T : RoomDatabase> Scope.databaseBuilder(
    name: String
): RoomDatabase.Builder<T>
