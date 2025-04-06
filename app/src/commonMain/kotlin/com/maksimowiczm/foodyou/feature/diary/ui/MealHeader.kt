package com.maksimowiczm.foodyou.feature.diary.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun MealHeader(
    headline: @Composable () -> Unit,
    time: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    spacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
    nutrientsLayout: @Composable () -> Unit
) {
    Column(
        modifier = modifier
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.headlineMedium
        ) {
            headline()
        }
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.labelLarge
        ) {
            time()
        }
        spacer()
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            nutrientsLayout()
        }
    }
}

@Composable
internal fun NutrientsLayout(
    caloriesLabel: @Composable () -> Unit,
    proteinsLabel: @Composable () -> Unit,
    carbohydratesLabel: @Composable () -> Unit,
    fatsLabel: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelMedium
    ) {
        FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.unit_kcal)
                )
                caloriesLabel()
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.proteinsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_proteins_short)
                    )
                    proteinsLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.carbohydratesOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_carbohydrates_short)
                    )
                    carbohydratesLabel()
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides nutrientsPalette.fatsOnSurfaceContainer
                ) {
                    Text(
                        text = stringResource(Res.string.nutriment_fats_short)
                    )
                    fatsLabel()
                }
            }
        }
    }
}
