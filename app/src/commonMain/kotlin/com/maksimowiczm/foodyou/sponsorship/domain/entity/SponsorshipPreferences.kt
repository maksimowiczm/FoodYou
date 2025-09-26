package com.maksimowiczm.foodyou.sponsorship.domain.entity

import com.maksimowiczm.foodyou.common.domain.userpreferences.UserPreferences

/**
 * User preferences regarding sponsorships.
 *
 * @property remoteAllowed Indicates if user allows fetching sponsorships from remote source. We
 *   have to respect user's choice and not to connect to remote if user didn't allow it.
 *     @property shouldCleanLegacyEntities Indicates if legacy sponsorship entities (stored in old
 *       format) should be cleaned. Once cleaned, this flag should be set to false to avoid repeated
 *       cleanups.
 */
data class SponsorshipPreferences(
    val remoteAllowed: Boolean,
    val shouldCleanLegacyEntities: Boolean,
) : UserPreferences
