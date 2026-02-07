package com.maksimowiczm.foodyou.common.infrastructure

import androidx.room.RoomDatabase

expect inline fun <reified T : RoomDatabase> provideRoomDatabaseBuilder(): RoomDatabase.Builder<T>
