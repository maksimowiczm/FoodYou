package com.maksimowiczm.foodyou.common.infrastructure.room

import androidx.room.RoomDatabase
import org.koin.core.scope.Scope

expect inline fun <reified T : RoomDatabase> Scope.databaseBuilder(
    name: String
): RoomDatabase.Builder<T>
