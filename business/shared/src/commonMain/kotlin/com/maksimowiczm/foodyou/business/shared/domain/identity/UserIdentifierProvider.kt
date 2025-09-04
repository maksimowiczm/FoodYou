package com.maksimowiczm.foodyou.business.shared.domain.identity

/**
 * A provider for retrieving the current user's identifier.
 *
 * It should be used for services that requires user distinction, e.g. Open Food Facts when
 * uploading products.
 */
interface UserIdentifierProvider {
    suspend fun getUserIdentifier(): UserIdentifier
}
