package com.maksimowiczm.foodyou.openfoodfacts.domain

data class OpenFoodFactsSettings(
    val remoteEnabled: Boolean = false,
    val login: String? = null,
    val password: String? = null,
)
