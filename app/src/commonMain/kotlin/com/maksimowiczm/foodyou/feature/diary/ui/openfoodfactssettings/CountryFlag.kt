package com.maksimowiczm.foodyou.feature.diary.ui.openfoodfactssettings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.feature.system.data.model.Country

fun interface CountryFlag {
    @Composable
    operator fun invoke(country: Country, modifier: Modifier)
}
