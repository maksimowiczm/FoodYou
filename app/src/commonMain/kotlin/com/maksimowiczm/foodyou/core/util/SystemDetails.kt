package com.maksimowiczm.foodyou.core.util

data class Country(
    /**
     * Country code in ISO 3166 format.
     */
    val code: String,
    val name: String
) {
    companion object {
        val Poland = Country("PL", "Poland")
        val UnitedStates = Country("US", "United States")
    }
}

/**
 * Provides information about system-specific settings and utilities for date and locale operations.
 */
expect class SystemDetails {

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

/**
 * Checks if the system's default country is United States which is default locale for the app.
 */
val SystemDetails.isUS: Boolean
    get() = defaultCountry.code == "US"
