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
import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrient
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
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
    products: List<ProductWithMeasurement>,
    incompleteValue: (Float) -> (@Composable () -> Unit),
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
                val value = products.sumOf { it.calories }.formatClipZeros()
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
                val value = products.sumOf { it.proteins }.formatClipZeros()
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
                val value = products.sumOf { it.carbohydrates }.formatClipZeros()
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
                val summary = products.nutrient(Nutrient.Sugars)

                when (summary) {
                    is NutrientSummary.Incomplete -> incompleteValue(summary.value)()
                    is NutrientSummary.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = summary.value.formatClipZeros()
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
                val value = products.sumOf { it.fats }.formatClipZeros()
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
                val summary = products.nutrient(Nutrient.SaturatedFats)
                when (summary) {
                    is NutrientSummary.Incomplete -> incompleteValue(summary.value)()
                    is NutrientSummary.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = summary.value.formatClipZeros()
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
                val summary = products.nutrient(Nutrient.Salt)
                when (summary) {
                    is NutrientSummary.Incomplete -> incompleteValue(summary.value)()
                    is NutrientSummary.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = summary.value.formatClipZeros()
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
                val summary = products.nutrient(Nutrient.Fiber)
                when (summary) {
                    is NutrientSummary.Incomplete -> incompleteValue(summary.value)()
                    is NutrientSummary.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = summary.value.formatClipZeros()
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
                val summary = products.nutrient(Nutrient.Sodium)
                when (summary) {
                    is NutrientSummary.Incomplete -> incompleteValue(summary.value)()
                    is NutrientSummary.Complete -> {
                        val g = stringResource(Res.string.unit_gram_short)
                        val value = summary.value.formatClipZeros()
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

private sealed interface NutrientSummary {
    val value: Float

    @JvmInline
    value class Complete(override val value: Float) : NutrientSummary

    @JvmInline
    value class Incomplete(override val value: Float) : NutrientSummary
}

private fun List<ProductWithMeasurement>.nutrient(nutrient: Nutrient): NutrientSummary {
    var isComplete = true

    val value = sumOf {
        it.product.nutrients
            .get(nutrient, it.weight)
            .also {
                if (it == null) {
                    isComplete = false
                }
            } ?: 0f
    }

    return if (isComplete) {
        NutrientSummary.Complete(value)
    } else {
        NutrientSummary.Incomplete(value)
    }
}
