package com.maksimowiczm.foodyou.feature.settings.openfoodfactssettings.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.data.model.Country

fun interface CountryFlag {
    @Composable
    operator fun invoke(country: Country, modifier: Modifier)
}
