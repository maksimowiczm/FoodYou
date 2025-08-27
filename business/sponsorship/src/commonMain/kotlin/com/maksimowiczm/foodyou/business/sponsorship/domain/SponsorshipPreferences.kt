package com.maksimowiczm.foodyou.business.sponsorship.domain

/**
 * User preferences regarding sponsorships.
 *
 * @property remoteAllowed Indicates if user allows fetching sponsorships from remote source. We
 *   have to respect user's choice and not to connect to remote if user didn't allow it.
 */
data class SponsorshipPreferences(val remoteAllowed: Boolean)
