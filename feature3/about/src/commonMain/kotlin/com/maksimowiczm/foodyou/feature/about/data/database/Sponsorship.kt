package com.maksimowiczm.foodyou.feature.about.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Entity(
    indices = [
        Index(value = ["sponsorshipEpochSeconds"], unique = false)
    ]
)
data class Sponsorship(
    @PrimaryKey
    val id: Long,
    val sponsorName: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val sponsorshipEpochSeconds: Long,
    val method: SponsorshipMethod
) {
    @OptIn(ExperimentalTime::class)
    fun sponsorshipDate(timeZone: TimeZone = TimeZone.UTC) = Instant
        .fromEpochSeconds(sponsorshipEpochSeconds)
        .toLocalDateTime(timeZone)
}
