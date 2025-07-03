package com.maksimowiczm.foodyou.feature.about.data.network

import kotlinx.serialization.Serializable

@Serializable
internal data class NetworkSponsorship(
    val id: Long,
    val sponsor: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val sponsorshipDate: String,
    val method: String
)
