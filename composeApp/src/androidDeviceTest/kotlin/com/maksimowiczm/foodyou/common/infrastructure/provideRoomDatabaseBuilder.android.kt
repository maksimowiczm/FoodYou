package com.maksimowiczm.foodyou.common.infrastructure

import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.test.platform.app.InstrumentationRegistry

actual inline fun <reified T : RoomDatabase> provideRoomDatabaseBuilder(): RoomDatabase.Builder<T> {
    val instrumentation = InstrumentationRegistry.getInstrumentation()

    return Room.inMemoryDatabaseBuilder(context = instrumentation.context, klass = T::class.java)
}
