package com.maksimowiczm.foodyou.common.infrastructure

import androidx.room.RoomDatabase
import org.junit.AssumptionViolatedException

actual inline fun <reified T : RoomDatabase> provideRoomDatabaseBuilder(): RoomDatabase.Builder<T> {
    throw AssumptionViolatedException("Skipping Room tests on this platform")
}
