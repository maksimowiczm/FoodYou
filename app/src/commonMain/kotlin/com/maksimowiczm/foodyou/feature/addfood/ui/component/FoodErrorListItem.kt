package com.maksimowiczm.foodyou.feature.addfood.ui.component

import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.error_measurement_error
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodErrorListItem(headline: String, modifier: Modifier = Modifier) {
    ListItem(
        headlineContent = { Text(headline) },
        modifier = modifier,
        supportingContent = { Text(stringResource(Res.string.error_measurement_error)) },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            headlineColor = MaterialTheme.colorScheme.onErrorContainer,
            supportingColor = MaterialTheme.colorScheme.onErrorContainer,
            overlineColor = MaterialTheme.colorScheme.onErrorContainer
        )
    )
}
