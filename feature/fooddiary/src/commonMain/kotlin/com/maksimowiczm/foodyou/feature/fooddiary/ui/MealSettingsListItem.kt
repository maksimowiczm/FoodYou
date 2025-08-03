package com.maksimowiczm.foodyou.feature.fooddiary.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import com.maksimowiczm.foodyou.core.ui.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealSettingsListItem(
    onClick: () -> Unit,
    shape: Shape,
    color: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    SettingsListItem(
        icon = {
            Icon(
                imageVector = Icons.Outlined.Restaurant,
                contentDescription = null
            )
        },
        label = {
            Text(stringResource(Res.string.headline_meals))
        },
        supportingContent = {
            Text(stringResource(Res.string.neutral_set_your_meal_schedule))
        },
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor
    )
}
