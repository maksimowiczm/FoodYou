package com.maksimowiczm.foodyou.ui.personalize

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.core.ui.component.SettingsListItem
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PersonalizeNutritionFactsSettingsListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface
) {
    SettingsListItem(
        headlineContent = { Text(stringResource(Res.string.headline_nutrition_facts)) },
        onClick = onClick,
        modifier = modifier,
        supportingContent = {
            Text(stringResource(Res.string.description_personalize_nutrition_facts_short))
        },
        leadingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.List,
                contentDescription = null
            )
        },
        containerColor = containerColor,
        contentColor = contentColor
    )
}
