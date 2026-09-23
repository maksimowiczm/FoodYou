package com.maksimowiczm.foodyou.common.extension

import androidx.room3.*
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual inline fun <reified T : RoomDatabase> Scope.databaseBuilder(
    name: String
): RoomDatabase.Builder<T> =
    Room.databaseBuilder(context = androidContext(), klass = T::class.java, name = name)
