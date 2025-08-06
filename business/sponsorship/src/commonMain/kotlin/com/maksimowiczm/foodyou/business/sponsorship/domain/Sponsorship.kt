package com.maksimowiczm.foodyou.business.sponsorship.domain

import kotlinx.datetime.LocalDateTime

data class Sponsorship(
    val sponsorName: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val date: LocalDateTime,
    val method: String,
)
