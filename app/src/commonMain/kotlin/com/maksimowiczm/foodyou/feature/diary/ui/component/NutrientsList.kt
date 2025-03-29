package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.feature.diary.data.model.NutrientValue
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrients
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_fiber
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.nutriment_salt
import foodyou.app.generated.resources.nutriment_saturated_fats
import foodyou.app.generated.resources.nutriment_sodium
import foodyou.app.generated.resources.nutriment_sugars
import foodyou.app.generated.resources.unit_calories
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientsList(
    nutrients: Nutrients,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    Column(
        modifier = modifier
    ) {
        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.unit_calories)
                )
            },
            value = {
                val value = nutrients.calories.formatClipZeros()
                val kcal = stringResource(Res.string.unit_kcal)
                Text(text = "$value $kcal")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_proteins),
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )
            },
            value = {
                val value = nutrients.proteins.formatClipZeros()
                val g = stringResource(Res.string.unit_gram_short)
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_carbohydrates),
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )
            },
            value = {
                val value = nutrients.carbohydrates.formatClipZeros()
                val g = stringResource(Res.string.unit_gram_short)
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_sugars)
                )
            },
            value = {
                when (nutrients.sugars) {
                    is NutrientValue.Incomplete -> incompleteValue(nutrients.sugars)()
                    is NutrientValue.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = nutrients.sugars.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_fats),
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )
            },
            value = {
                val value = nutrients.fats.formatClipZeros()
                val g = stringResource(Res.string.unit_gram_short)
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_saturated_fats)
                )
            },
            value = {
                when (nutrients.saturatedFats) {
                    is NutrientValue.Incomplete -> incompleteValue(nutrients.saturatedFats)()
                    is NutrientValue.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = nutrients.saturatedFats.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_salt)
                )
            },
            value = {
                when (nutrients.salt) {
                    is NutrientValue.Incomplete -> incompleteValue(nutrients.salt)()
                    is NutrientValue.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = nutrients.salt.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_fiber)
                )
            },
            value = {
                when (nutrients.fiber) {
                    is NutrientValue.Incomplete -> incompleteValue(nutrients.fiber)()
                    is NutrientValue.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = nutrients.fiber.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider(Modifier.padding(horizontal = 48.dp))

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_sodium)
                )
            },
            value = {
                when (nutrients.sodium) {
                    is NutrientValue.Incomplete -> incompleteValue(nutrients.sodium)()
                    is NutrientValue.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = nutrients.sodium.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun NutrientListItem(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            label()
        }
        value()
    }
}
