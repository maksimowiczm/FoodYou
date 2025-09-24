package com.maksimowiczm.foodyou.sponsorship.infrastructure.foodyousponsors

import kotlinx.serialization.Serializable

@Serializable
internal data class PagedSponsorshipsResponse(
    val sponsorships: List<NetworkSponsorship>,
    val totalSize: Int,
    val hasMoreBefore: Boolean,
    val hasMoreAfter: Boolean,
) {
    fun isEmpty(): Boolean = sponsorships.isEmpty()
}

@Serializable
internal data class NetworkSponsorship(
    val id: Long,
    val sponsor: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val sponsorshipDate: String,
    val method: String,
)
