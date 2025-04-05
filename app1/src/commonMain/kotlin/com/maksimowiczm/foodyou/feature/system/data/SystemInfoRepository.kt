package com.maksimowiczm.foodyou.feature.system.data

import com.maksimowiczm.foodyou.feature.system.data.model.Country

/**
 * Provides information about system-specific settings and utilities for date and locale operations.
 */
expect class SystemInfoRepository {

    /**
     * The default country code of the system's locale.
     *
     * This value is derived from the system configuration and represents the ISO 3166-1 alpha-2 country code.
     */
    val defaultCountry: Country

    /**
     * The list of countries available on the system.
     */
    val countries: List<Country>
}
