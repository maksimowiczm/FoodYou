package com.maksimowiczm.foodyou.feature.settings.mealssettings.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.CreateMealSettingsCardTestTags.CREATE_BUTTON
import com.maksimowiczm.foodyou.feature.settings.mealssettings.ui.CreateMealSettingsCardTestTags.CREATE_MEAL_SETTINGS_CARD
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add_meal
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun CreateMealSettingsCard(
    isCreating: Boolean,
    onCreatingChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    if (isCreating) {
        val viewModel = CreateMealSettingsCardViewModel(
            diaryRepository = koinInject(),
            stringFormatRepository = koinInject(),
            coroutineScope = coroutineScope
        )

        val state = rememberMealSettingsCardState()

        MealSettingsCard(
            state = state,
            onDelete = { onCreatingChange(false) },
            onUpdate = {
                viewModel.createMeal(
                    name = state.nameInput.value,
                    from = state.fromInput.value,
                    to = if (state.isAllDay) {
                        state.fromInput.value
                    } else {
                        state.toInput.value
                    }
                )
                onCreatingChange(false)
            },
            formatTime = viewModel::formatTime,
            showDeleteDialog = false,
            action = null,
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
