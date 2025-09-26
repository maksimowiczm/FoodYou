package com.maksimowiczm.foodyou.sponsorship.infrastructure.foodyousponsors

import com.maksimowiczm.foodyou.sponsorship.infrastructure.NetworkSponsorship
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
