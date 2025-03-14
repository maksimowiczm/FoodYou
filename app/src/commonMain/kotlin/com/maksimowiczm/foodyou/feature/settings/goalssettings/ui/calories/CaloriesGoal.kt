package com.maksimowiczm.foodyou.feature.settings.goalssettings.ui.calories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.data.model.DailyGoals
import com.maksimowiczm.foodyou.data.model.defaultGoals
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CaloriesGoal(goals: DailyGoals, onSave: (DailyGoals) -> Unit, modifier: Modifier = Modifier) {
    val state = rememberCaloriesFoalFormState(goals)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(Res.string.headline_calories_goal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = stringResource(Res.string.description_calories_goal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            CaloriesGoalForm(
                state = state
            )
        }

        Button(
            onClick = { onSave(state.intoDailyGoals()) },
            enabled = state.isValid
        ) {
            Text(stringResource(Res.string.action_save))
        }
    }
}

@Preview
@Composable
private fun CaloriesGoalPreview() {
    FoodYouTheme {
        Surface {
            CaloriesGoal(
                goals = defaultGoals(),
                onSave = {}
            )
        }
    }
}
