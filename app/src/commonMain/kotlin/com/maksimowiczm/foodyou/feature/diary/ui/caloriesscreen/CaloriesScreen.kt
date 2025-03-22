package com.maksimowiczm.foodyou.feature.diary.ui.caloriesscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrient
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus
import com.maksimowiczm.foodyou.feature.diary.ui.caloriescard.ValueStatus.Companion.asValueStatus
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCardDefaults
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.nutriment_sugars
import foodyou.app.generated.resources.unit_calories
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CaloriesScreen(
    date: LocalDate,
    modifier: Modifier = Modifier,
    viewModel: CaloriesScreenViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(date).collectAsStateWithLifecycle(null)

    if (diaryDay != null) {
        CaloriesScreen(
            diaryDay = diaryDay!!,
            formatDate = viewModel::formatDate,
            modifier = modifier
        )
    } else {
        Surface(modifier) {
            Spacer(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun CaloriesScreen(
    diaryDay: DiaryDay,
    formatDate: (LocalDate) -> String,
    modifier: Modifier = Modifier
) {
    val goals = diaryDay.dailyGoals
    val date = diaryDay.date
    val headerColor = FoodYouHomeCardDefaults.colors().containerColor
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    val topBar = @Composable {
        val bottomInset = WindowInsets.systemBars.only(WindowInsetsSides.Bottom)

        val insets = WindowInsets.systemBars
            .union(WindowInsets.displayCutout)
            .exclude(bottomInset)
            .asPaddingValues()

        Surface(
            modifier = Modifier,
            color = headerColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(insets)
                    .consumeWindowInsets(insets)
            ) {
                Text(
                    text = formatDate(date),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }

    Surface(
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                topBar()
            }

            item {
                Spacer(Modifier.height(16.dp))
            }

            item {
                val value = diaryDay.totalCalories
                val goal = goals.calories
                val status = value.asValueStatus(goals.calories.toFloat())
                val color = LocalTextStyle.current
                val unitString = stringResource(Res.string.unit_kcal)

                val string = remember {
                    buildAnnotatedString {
                        withStyle(
                            typography.headlineSmall.merge(
                                color = when (status) {
                                    ValueStatus.Exceeded -> colorScheme.error
                                    ValueStatus.Remaining,
                                    ValueStatus.Achieved -> color.color
                                }
                            ).toSpanStyle()
                        ) {
                            append(diaryDay.totalCalories.formatClipZeros("%.2f"))
                        }
                        withStyle(
                            typography.bodyMedium.merge(
                                color = colorScheme.outline
                            ).toSpanStyle()
                        ) {
                            append(" / $goal $unitString")
                        }
                    }
                }

                DetailListItem(
                    title = { Text(stringResource(Res.string.unit_calories)) },
                    value = { Text(string) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                val value = diaryDay.totalProteins
                val goal = goals.proteinsAsGrams
                val status = value.asValueStatus(goals.proteinsAsGrams.toFloat())
                val color = LocalTextStyle.current
                val unitString = stringResource(Res.string.unit_gram_short)

                val string = remember {
                    buildAnnotatedString {
                        withStyle(
                            typography.headlineSmall.merge(
                                color = when (status) {
                                    ValueStatus.Exceeded -> colorScheme.error
                                    ValueStatus.Remaining,
                                    ValueStatus.Achieved -> color.color
                                }
                            ).toSpanStyle()
                        ) {
                            append(diaryDay.totalProteins.formatClipZeros("%.2f"))
                        }
                        withStyle(
                            typography.bodyMedium.merge(
                                color = colorScheme.outline
                            ).toSpanStyle()
                        ) {
                            append(" / $goal $unitString")
                        }
                    }
                }

                DetailListItem(
                    title = { Text(stringResource(Res.string.nutriment_proteins)) },
                    value = { Text(string) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                val value = diaryDay.totalCarbohydrates
                val goal = goals.carbohydratesAsGrams
                val status = value.asValueStatus(goals.carbohydratesAsGrams.toFloat())
                val color = LocalTextStyle.current
                val unitString = stringResource(Res.string.unit_gram_short)

                val string = remember {
                    buildAnnotatedString {
                        withStyle(
                            typography.headlineSmall.merge(
                                color = when (status) {
                                    ValueStatus.Exceeded -> colorScheme.error
                                    ValueStatus.Remaining,
                                    ValueStatus.Achieved -> color.color
                                }
                            ).toSpanStyle()
                        ) {
                            append(diaryDay.totalCarbohydrates.formatClipZeros("%.2f"))
                        }
                        withStyle(
                            typography.bodyMedium.merge(
                                color = colorScheme.outline
                            ).toSpanStyle()
                        ) {
                            append(" / $goal $unitString")
                        }
                    }
                }

                DetailListItem(
                    title = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                    value = { Text(string) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                val value = diaryDay.total(Nutrient.Sugars).value
                val color = LocalTextStyle.current
                val unitString = stringResource(Res.string.unit_gram_short)

                val string = remember {
                    buildAnnotatedString {
                        withStyle(
                            typography.headlineSmall.merge(
                                color = color.color
                            ).toSpanStyle()
                        ) {
                            append(value.formatClipZeros("%.2f"))
                        }
                        withStyle(
                            typography.bodyMedium.merge(
                                color = colorScheme.outline
                            ).toSpanStyle()
                        ) {
                            append(unitString)
                        }
                    }
                }

                DetailListItem(
                    title = { Text(stringResource(Res.string.nutriment_sugars)) },
                    value = { Text(string) },
                    modifier = Modifier.padding(
                        horizontal = 16.dp
                    ).padding(start = 8.dp).padding(start = 8.dp)
                )
            }

            item {
                Spacer(Modifier.height(24.dp))
            }

            item {
                val value = diaryDay.totalFats
                val goal = goals.fatsAsGrams
                val status = value.asValueStatus(goals.fatsAsGrams.toFloat())
                val color = LocalTextStyle.current
                val unitString = stringResource(Res.string.unit_gram_short)

                val string = remember {
                    buildAnnotatedString {
                        withStyle(
                            typography.headlineSmall.merge(
                                color = when (status) {
                                    ValueStatus.Exceeded -> colorScheme.error
                                    ValueStatus.Remaining,
                                    ValueStatus.Achieved -> color.color
                                }
                            ).toSpanStyle()
                        ) {
                            append(diaryDay.totalFats.formatClipZeros("%.2f"))
                        }
                        withStyle(
                            typography.bodyMedium.merge(
                                color = colorScheme.outline
                            ).toSpanStyle()
                        ) {
                            append(" / $goal $unitString")
                        }
                    }
                }

                DetailListItem(
                    title = { Text(stringResource(Res.string.nutriment_fats)) },
                    value = { Text(string) },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            item {
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun DetailListItem(
    title: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom
    ) {
        title()
        Spacer(Modifier.weight(1f))
        value()
    }
}
