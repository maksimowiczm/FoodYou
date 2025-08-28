package com.maksimowiczm.foodyou.business.shared.infrastructure.room.sponsorship

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Sponsorship",
    indices = [Index(value = ["sponsorshipEpochSeconds"], unique = false)],
)
data class SponsorshipEntity(
    @PrimaryKey val id: Long,
    val sponsorName: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val sponsorshipEpochSeconds: Long,
    val method: String,
)
