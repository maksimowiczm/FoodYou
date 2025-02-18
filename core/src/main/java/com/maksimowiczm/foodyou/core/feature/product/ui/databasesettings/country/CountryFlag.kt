package com.maksimowiczm.foodyou.core.feature.product.ui.databasesettings.country

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.core.feature.system.data.model.Country

fun interface CountryFlag {
    @Composable
    operator fun invoke(
        country: Country,
        modifier: Modifier
    )
}
