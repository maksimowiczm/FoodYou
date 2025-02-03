package com.maksimowiczm.foodyou.core.feature.diary.ui.mealscard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.R
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

@Composable
fun MealsCard(
    diaryDay: DiaryDay,
    onAddClick: (Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            val items = diaryDay.meals

            items.forEachIndexed { index, meal ->
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
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            }
        },
        modifier = modifier,
        leadingContent = icon,
        supportingContent = {
            Text(
                text = "$value " + stringResource(R.string.unit_kcal),
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
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
