package com.maksimowiczm.foodyou.core.feature.diary.ui.goalssettings.calories

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DailyGoals
import com.maksimowiczm.foodyou.core.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

@Composable
fun CaloriesGoal(goals: DailyGoals, onSave: (DailyGoals) -> Unit, modifier: Modifier = Modifier) {
    val state = rememberCaloriesFoalFormState(goals)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.headline_calories_goal),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            text = stringResource(R.string.description_calories_goal),
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
            onClick = {
                state.intoDailyGoals()?.let { onSave(it) }
            },
            enabled = state.isValid
        ) {
            Text(stringResource(R.string.action_save))
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
