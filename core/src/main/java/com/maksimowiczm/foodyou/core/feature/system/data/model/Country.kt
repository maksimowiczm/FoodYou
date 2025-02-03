package com.maksimowiczm.foodyou.core.feature.system.data.model

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
