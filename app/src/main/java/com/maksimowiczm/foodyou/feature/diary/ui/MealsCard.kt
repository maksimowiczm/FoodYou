package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.feature.diary.ui.theme.LocalDiaryPalette
import com.maksimowiczm.foodyou.ui.component.ProgressIndicator
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun MealsCard(
    diaryDay: DiaryDay,
    onAddClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val items = diaryDay.meals

            items.forEachIndexed { index, meal ->
                val goal = diaryDay.dailyGoals.calorieGoal(meal)
                val uiData = meal.asMealUiData()

                MaterialMealItem(
                    icon = {
                        Icon(
                            painter = painterResource(uiData.icon),
                            contentDescription = null
                        )
                    },
                    title = {
                        Text(
                            text = stringResource(uiData.title)
                        )
                    },
                    value = diaryDay.totalCalories(meal),
                    goalValue = goal,
                    onAddClick = { onAddClick(meal) },
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                if (index < items.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialMealItem(
    icon: @Composable () -> Unit,
    title: @Composable () -> Unit,
    value: Int,
    goalValue: Int,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(targetValue = value.toFloat())

    ListItem(
        headlineContent = {
            Row(
                modifier = Modifier.height(40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleLarge
                ) {
                    title()
                }

                if (value >= goalValue) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(LocalDiaryPalette.current.goalsFulfilledColor)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.Black
                        )
                    }
                }
            }
        },
        modifier = modifier,
        leadingContent = icon,
        supportingContent = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .width(IntrinsicSize.Max),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier.width(IntrinsicSize.Min)
                ) {
                    ProgressIndicator(
                        modifier = Modifier.size(width = 50.dp, height = 6.dp),
                        progress = { animatedValue / goalValue },
                        progressColor = if (animatedValue >= goalValue) {
                            LocalDiaryPalette.current.goalsFulfilledColor
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.x_out_of_y_kcal, value, goalValue),
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        },
        trailingContent = {
            FilledIconButton(
                onClick = onAddClick
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.action_add_product)
                )
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}

private sealed interface MealUiData {
    val icon: Int
    val title: Int

    data object Breakfast : MealUiData {
        override val icon: Int = R.drawable.ic_egg_alt_24
        override val title: Int = R.string.meal_name_breakfast
    }

    data object Lunch : MealUiData {
        override val icon: Int = R.drawable.ic_lunch_dining_24
        override val title: Int = R.string.meal_name_lunch
    }

    data object Dinner : MealUiData {
        override val icon: Int = R.drawable.ic_dinner_dining_24
        override val title: Int = R.string.meal_name_dinner
    }

    data object Snacks : MealUiData {
        override val icon: Int = R.drawable.ic_icecream_24
        override val title: Int = R.string.meal_name_snacks
    }
}

private fun Meal.asMealUiData(): MealUiData = when (this) {
    Meal.Breakfast -> MealUiData.Breakfast
    Meal.Lunch -> MealUiData.Lunch
    Meal.Dinner -> MealUiData.Dinner
    Meal.Snacks -> MealUiData.Snacks
}

@PreviewLightDark
@Composable
private fun MealsCardPreview() {
    val diaryDay = DiaryDayPreviewParameterProvider().values.first()

    FoodYouTheme {
        MealsCard(
            diaryDay = diaryDay,
            onAddClick = {}
        )
    }
}
