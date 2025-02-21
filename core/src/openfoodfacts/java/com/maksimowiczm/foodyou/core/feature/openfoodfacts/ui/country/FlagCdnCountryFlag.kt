package com.maksimowiczm.foodyou.core.feature.openfoodfacts.ui.country

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

val flagCdnCountryFlag = CountryFlag { country, modifier ->
    val flagUrl = "https://flagcdn.com/w160/${country.code.lowercase()}.jpg"
    var showCode by remember { mutableStateOf(true) }

    Box(modifier) {
        if (showCode) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = country.code
            )
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            onSuccess = { showCode = false },
            onError = { showCode = true },
            model = flagUrl,
            contentDescription = country.name
        )
    }
}
