package com.maksimowiczm.foodyou.feature.meal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.headline_meals
import foodyou.app.generated.resources.neutral_set_your_meal_schedule
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = {
            Text(stringResource(Res.string.headline_meals))
        },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.neutral_set_your_meal_schedule))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
