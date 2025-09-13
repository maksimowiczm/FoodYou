package com.maksimowiczm.foodyou.feature.food.shared.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.business.shared.domain.settings.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.shared.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.shared.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.feature.shared.ui.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.compose.utility.formatClipZeros
import com.maksimowiczm.foodyou.shared.domain.food.NutrientValue
import com.maksimowiczm.foodyou.shared.domain.food.NutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientList(
    facts: NutritionFacts,
    modifier: Modifier = Modifier,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit) =
        NutrientListDefaults::incompleteValue,
) {
    val order = LocalNutrientsOrder.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Energy(facts, incompleteValue)

        order.forEach {
            when (it) {
                NutrientsOrder.Proteins -> Proteins(facts, incompleteValue)
                NutrientsOrder.Fats -> Fats(facts, incompleteValue)
                NutrientsOrder.Carbohydrates -> Carbohydrates(facts, incompleteValue)
                NutrientsOrder.Other -> Other(facts, incompleteValue)
                NutrientsOrder.Vitamins -> Vitamins(facts, incompleteValue)
                NutrientsOrder.Minerals -> Minerals(facts, incompleteValue)
            }
        }
    }
}

@Composable
private fun Energy(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val energyFormatter = LocalEnergyFormatter.current

    Nutrient(
        label = { Text(stringResource(Res.string.unit_energy)) },
        value = {
            when (val nut = facts.energy) {
                is NutrientValue.Complete -> Text(energyFormatter.formatEnergy(nut.value))
                is NutrientValue.Incomplete -> incompleteValue(nut)()
            }
        },
        modifier = modifier,
        contentPadding = contentPadding,
    )
}

@Composable
private fun Proteins(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    Nutrient(
        label = { Text(stringResource(Res.string.nutriment_proteins)) },
        value = { NutrientDisplay(facts.proteins, incompleteValue) },
        modifier = modifier,
        contentPadding = contentPadding,
        shape = MaterialTheme.shapes.medium,
        containerColor =
            LocalNutrientsPalette.current.proteinsOnSurfaceContainer.copy(alpha = .33f),
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun Fats(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val fats = @Composable { NutrientDisplay(facts.fats, incompleteValue) }
    val saturatedFats = @Composable { NutrientDisplay(facts.saturatedFats, incompleteValue) }
    val transFats = @Composable { NutrientDisplay(facts.transFats, incompleteValue) }
    val monounsaturatedFats =
        @Composable { NutrientDisplay(facts.monounsaturatedFats, incompleteValue) }
    val polyunsaturatedFats =
        @Composable { NutrientDisplay(facts.polyunsaturatedFats, incompleteValue) }
    val omega3Fats = @Composable { NutrientDisplay(facts.omega3, incompleteValue) }
    val omega6Fats = @Composable { NutrientDisplay(facts.omega6, incompleteValue) }

    NutrientGroup(
        title = {
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                value = fats,
                contentPadding = contentPadding,
                shape = MaterialTheme.shapes.medium,
                containerColor =
                    LocalNutrientsPalette.current.fatsOnSurfaceContainer.copy(alpha = .33f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        },
        modifier = modifier,
    ) {
        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
            value = saturatedFats,
        )
        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_trans_fats)) },
            value = transFats,
        )
        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_monounsaturated_fats)) },
            value = monounsaturatedFats,
        )

        NutrientGroup(
            title = {
                Nutrient(
                    label = { Text(stringResource(Res.string.nutriment_polyunsaturated_fats)) },
                    value = polyunsaturatedFats,
                )
            }
        ) {
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_omega_3)) },
                value = omega3Fats,
            )
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_omega_6)) },
                value = omega6Fats,
            )
        }
    }
}

@Composable
private fun Carbohydrates(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    NutrientGroup(
        title = {
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                value = { NutrientDisplay(facts.carbohydrates, incompleteValue) },
                modifier = modifier,
                contentPadding = contentPadding,
                shape = MaterialTheme.shapes.medium,
                containerColor =
                    LocalNutrientsPalette.current.carbohydratesOnSurfaceContainer.copy(
                        alpha = .33f
                    ),
                contentColor = MaterialTheme.colorScheme.onSurface,
            )
        }
    ) {
        NutrientGroup(
            title = {
                Nutrient(
                    label = { Text(stringResource(Res.string.nutriment_sugars)) },
                    value = { NutrientDisplay(facts.sugars, incompleteValue) },
                )
            }
        ) {
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_added_sugars)) },
                value = { NutrientDisplay(facts.addedSugars, incompleteValue) },
            )
        }
        NutrientGroup(
            title = {
                Nutrient(
                    label = { Text(stringResource(Res.string.nutriment_fiber)) },
                    value = { NutrientDisplay(facts.dietaryFiber, incompleteValue) },
                )
            }
        ) {
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_soluble_fiber)) },
                value = { NutrientDisplay(facts.solubleFiber, incompleteValue) },
            )
            Nutrient(
                label = { Text(stringResource(Res.string.nutriment_insoluble_fiber)) },
                value = { NutrientDisplay(facts.insolubleFiber, incompleteValue) },
            )
        }
    }
}

@Composable
private fun Other(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val mg = stringResource(Res.string.unit_milligram_short)

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.headline_other),
            modifier = Modifier.padding(contentPadding),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_salt)) },
            value = { NutrientDisplay(facts.salt, incompleteValue) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_cholesterol)) },
            value = { NutrientDisplay(facts.cholesterol * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.nutriment_caffeine)) },
            value = { NutrientDisplay(facts.caffeine * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun Vitamins(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val mg = stringResource(Res.string.unit_milligram_short)
    val mcg = stringResource(Res.string.unit_microgram_short)

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.headline_vitamins),
            modifier = Modifier.padding(contentPadding),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_a)) },
            value = { NutrientDisplay(facts.vitaminA * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b1)) },
            value = { NutrientDisplay(facts.vitaminB1 * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b2)) },
            value = { NutrientDisplay(facts.vitaminB2 * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b3)) },
            value = { NutrientDisplay(facts.vitaminB3 * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b5)) },
            value = { NutrientDisplay(facts.vitaminB5 * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b6)) },
            value = { NutrientDisplay(facts.vitaminB6 * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b7)) },
            value = { NutrientDisplay(facts.vitaminB7 * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b9)) },
            value = { NutrientDisplay(facts.vitaminB9 * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_b12)) },
            value = { NutrientDisplay(facts.vitaminB12 * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_c)) },
            value = { NutrientDisplay(facts.vitaminC * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_d)) },
            value = { NutrientDisplay(facts.vitaminD * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_e)) },
            value = { NutrientDisplay(facts.vitaminE * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        Nutrient(
            label = { Text(stringResource(Res.string.vitamin_k)) },
            value = { NutrientDisplay(facts.vitaminK * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun Minerals(
    facts: NutritionFacts,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
) {
    val mg = stringResource(Res.string.unit_milligram_short)
    val mcg = stringResource(Res.string.unit_microgram_short)

    Column(modifier = modifier) {
        Text(
            text = stringResource(Res.string.headline_minerals),
            modifier = Modifier.padding(contentPadding),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )

        // Manganese
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_manganese)) },
            value = { NutrientDisplay(facts.manganese * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Magnesium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_magnesium)) },
            value = { NutrientDisplay(facts.magnesium * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Potassium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_potassium)) },
            value = { NutrientDisplay(facts.potassium * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Calcium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_calcium)) },
            value = { NutrientDisplay(facts.calcium * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Copper
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_copper)) },
            value = { NutrientDisplay(facts.copper * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Zinc
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_zinc)) },
            value = { NutrientDisplay(facts.zinc * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Sodium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_sodium)) },
            value = { NutrientDisplay(facts.sodium * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Iron
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_iron)) },
            value = { NutrientDisplay(facts.iron * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Phosphorus
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_phosphorus)) },
            value = { NutrientDisplay(facts.phosphorus * 1_000.0, incompleteValue, mg) },
            contentPadding = contentPadding,
        )

        // Selenium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_selenium)) },
            value = { NutrientDisplay(facts.selenium * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )

        // Chromium
        Nutrient(
            label = { Text(stringResource(Res.string.mineral_chromium)) },
            value = { NutrientDisplay(facts.chromium * 1_000_000.0, incompleteValue, mcg) },
            contentPadding = contentPadding,
        )
    }
}

@Composable
private fun Nutrient(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    shape: Shape = RectangleShape,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = LocalContentColor.current,
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shape = shape,
    ) {
        Row(
            modifier = Modifier.padding(contentPadding).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                label()
                value()
            }
        }
    }
}

@Composable
private fun NutrientGroup(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        title()
        Column(modifier = Modifier.padding(start = 16.dp)) { content() }
    }
}

@Composable
private fun NutrientDisplay(
    nutrientValue: NutrientValue,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    suffix: String = stringResource(Res.string.unit_gram_short),
) {
    when (nutrientValue) {
        is NutrientValue.Complete -> {
            val value = nutrientValue.value.formatClipZeros() + " " + suffix
            Text(value)
        }

        is NutrientValue.Incomplete -> {
            incompleteValue(nutrientValue)()
        }
    }
}

object NutrientListDefaults {
    val incompletePrefix: String
        @Composable get() = "*"

    fun incompleteValue(value: NutrientValue.Incomplete): @Composable () -> Unit = {
        val value = value.value?.formatClipZeros()

        val str =
            value?.let { "$incompletePrefix $it ${stringResource(Res.string.unit_gram_short)}" }
                ?: stringResource(Res.string.not_available_short)

        Text(text = str, color = MaterialTheme.colorScheme.outline)
    }
}
