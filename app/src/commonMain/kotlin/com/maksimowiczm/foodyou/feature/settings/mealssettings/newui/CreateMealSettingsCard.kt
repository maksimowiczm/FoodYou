package com.maksimowiczm.foodyou.feature.settings.mealssettings.newui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.CreateMealSettingsCardTestTags.CREATE_BUTTON
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add_meal
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateMealSettingsCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.testTag(CREATE_BUTTON),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MealSettingsCardDefaults.colors().containerColor,
            contentColor = MealSettingsCardDefaults.colors().contentColor
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.action_add_meal)
            )
        }
    }
}

object CreateMealSettingsCardTestTags {
    const val CREATE_BUTTON = "CreateButton"
}
