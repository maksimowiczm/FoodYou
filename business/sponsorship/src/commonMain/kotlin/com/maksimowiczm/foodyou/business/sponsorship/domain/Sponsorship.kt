package com.maksimowiczm.foodyou.business.sponsorship.domain

import kotlinx.datetime.LocalDateTime

data class Sponsorship(
    val id: Long,
    val sponsorName: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val dateTime: LocalDateTime,
    val method: String,
)
