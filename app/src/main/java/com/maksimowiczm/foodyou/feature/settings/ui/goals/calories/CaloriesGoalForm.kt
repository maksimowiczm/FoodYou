package com.maksimowiczm.foodyou.feature.settings.ui.goals.calories

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.diary.data.model.defaultGoals
import com.maksimowiczm.foodyou.feature.product.data.model.WeightUnit
import com.maksimowiczm.foodyou.feature.product.ui.res.stringResourceShort
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun CaloriesGoalForm(
    state: CaloriesGoalFormState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = state.caloriesTextFieldValue,
            onValueChange = state::onCaloriesChanged,
            label = { Text(stringResource(R.string.unit_calories)) },
            suffix = { Text(stringResource(R.string.unit_kcal)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            maxLines = 1
        )

        Column {
            Text(
                text = stringResource(R.string.nutriment_proteins)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.proteinsGramsTextFieldValue,
                    onValueChange = state::onProteinsGramsChanged,
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.proteinsPercentageTextFieldValue,
                    onValueChange = state::onProteinsPercentageChanged,
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
            }
        }

        Column {
            Text(
                text = stringResource(R.string.nutriment_carbohydrates)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.carbsGramsTextFieldValue,
                    onValueChange = state::onCarbohydratesGramsChanged,
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.carbsPercentageTextFieldValue,
                    onValueChange = state::onCarbohydratesPercentageChanged,
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
            }
        }

        Column {
            Text(
                text = stringResource(R.string.nutriment_fats)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.fatsGramsTextFieldValue,
                    onValueChange = state::onFatsGramsChanged,
                    suffix = { Text(WeightUnit.Gram.stringResourceShort()) },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = state.fatsPercentageTextFieldValue,
                    onValueChange = state::onFatsPercentageChanged,
                    suffix = { Text("%") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    isError = state.isError,
                    maxLines = 1
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = stringResource(R.string.neutral_total) + ": ${state.totalPercentage}%",
                style = MaterialTheme.typography.bodyLarge
            )
            AnimatedVisibility(
                visible = state.isError
            ) {
                Text(
                    text = stringResource(R.string.neutral_total_value_must_be_100),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun CaloriesGoalFormPreview() {
    val goals = defaultGoals().copy(
        proteins = 1f
    )

    FoodYouTheme {
        CaloriesGoalForm(
            state = rememberCaloriesFoalFormState(goals)
        )
    }
}
