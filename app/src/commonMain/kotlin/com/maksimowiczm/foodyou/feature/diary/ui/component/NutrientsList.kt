package com.maksimowiczm.foodyou.feature.diary.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ext.sumOf
import com.maksimowiczm.foodyou.feature.diary.data.model.Nutrient
import com.maksimowiczm.foodyou.feature.diary.data.model.Product
import com.maksimowiczm.foodyou.feature.diary.data.model.ProductWithMeasurement
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.description_incomplete_nutrition_data
import foodyou.app.generated.resources.headline_incomplete_products
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
    onProductClick: (Product) -> Unit,
    modifier: Modifier = Modifier,
    showIncompleteProducts: Boolean = true
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
                val g = stringResource(Res.string.unit_gram_short)
                val prefix = summary.prefix()
                val value = summary.value.formatClipZeros()
                Text(
                    text = "$prefix $value $g",
                    color = summary.color()
                )
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
                val g = stringResource(Res.string.unit_gram_short)
                val prefix = summary.prefix()
                val value = summary.value.formatClipZeros()
                Text(
                    text = "$prefix $value $g",
                    color = summary.color(),
                    maxLines = 1
                )
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
                val g = stringResource(Res.string.unit_gram_short)
                val prefix = summary.prefix()
                val value = summary.value.formatClipZeros()
                Text(
                    text = "$prefix $value $g",
                    color = summary.color()
                )
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
                val g = stringResource(Res.string.unit_gram_short)
                val prefix = summary.prefix()
                val value = summary.value.formatClipZeros()
                Text(
                    text = "$prefix $value $g",
                    color = summary.color()
                )
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
                val g = stringResource(Res.string.unit_gram_short)
                val prefix = summary.prefix()
                val value = summary.value.formatClipZeros()
                Text(
                    text = "$prefix $value $g",
                    color = summary.color()
                )
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        val anyProductIncomplete = products.any { !it.product.nutrients.isComplete }

        // Display incomplete products
        if (anyProductIncomplete) {
            Column(
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(
                    text =
                    "$PREFIX_INCOMPLETE_NUTRIENT_DATA " +
                        stringResource(Res.string.description_incomplete_nutrition_data),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )

                if (showIncompleteProducts) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = stringResource(Res.string.headline_incomplete_products),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )

                    val products = products
                        .map { it.product }
                        .distinct()
                        .filter { !it.nutrients.isComplete }

                    products.forEach { product ->
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Companion.Center,
                            modifier = Modifier.clickable(
                                interactionSource = remember {
                                    MutableInteractionSource()
                                },
                                indication = null,
                                onClick = {
                                    onProductClick(product)
                                }
                            )
                        )
                    }
                }
            }
        }
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

@Composable
private fun NutrientSummary.color(): Color = when (this) {
    is NutrientSummary.Complete -> LocalTextStyle.current.color
    is NutrientSummary.Incomplete -> MaterialTheme.colorScheme.outline
}

const val PREFIX_INCOMPLETE_NUTRIENT_DATA = "*"

private fun NutrientSummary.prefix(): String = when (this) {
    is NutrientSummary.Complete -> ""
    is NutrientSummary.Incomplete -> PREFIX_INCOMPLETE_NUTRIENT_DATA
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
