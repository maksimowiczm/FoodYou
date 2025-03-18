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
import com.maksimowiczm.foodyou.feature.settings.mealssettings.newui.CreateMealSettingsCardTestTags.CREATE_MEAL_SETTINGS_CARD
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add_meal
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun CreateMealSettingsCard(
    isCreating: Boolean,
    onCreatingChange: (Boolean) -> Unit,
    onCreate: (name: String, from: LocalTime, to: LocalTime) -> Unit,
    formatTime: (LocalTime) -> String,
    modifier: Modifier = Modifier
) {
    if (isCreating) {
        val state = rememberMealSettingsCardState()

        MealSettingsCard(
            state = state,
            onDelete = { onCreatingChange(false) },
            onSave = {
                onCreate(
                    state.nameInput.value.text,
                    state.fromTimeInput.value,
                    if (state.isAllDay.value) {
                        state.fromTimeInput.value
                    } else {
                        state.toTimeInput.value
                    }
                )
                onCreatingChange(false)
            },
            formatTime = formatTime,
            action = null,
            shouldShowDeleteDialog = false,
            modifier = modifier.testTag(CREATE_MEAL_SETTINGS_CARD)
        )
    } else {
        Card(
            onClick = { onCreatingChange(true) },
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
}

object CreateMealSettingsCardTestTags {
    const val CREATE_BUTTON = "CreateButton"
    const val CREATE_MEAL_SETTINGS_CARD = "CreateMealSettingsCard"
}
