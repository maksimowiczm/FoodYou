package com.maksimowiczm.foodyou.feature.about.data.database

import androidx.room.TypeConverter

@Suppress("unused")
class SponsorMethodConverter {
    @TypeConverter
    fun fromOrdinal(ordinal: Int): SponsorshipMethod = SponsorshipMethod.entries[ordinal]

    @TypeConverter
    fun toOrdinal(method: SponsorshipMethod): Int = method.ordinal
}
