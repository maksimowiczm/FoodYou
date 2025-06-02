package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.NutrientValue
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutritionFactsList(
    facts: NutritionFacts,
    modifier: Modifier = Modifier,
    incompleteValue: (
        NutrientValue.Incomplete
    ) -> (@Composable () -> Unit) = NutrientsListDefaults::incompleteValue
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val g = stringResource(Res.string.unit_gram_short)
    val mg = stringResource(Res.string.unit_milligram_short)
    val ug = stringResource(Res.string.unit_microgram_short)

    Column(modifier) {
        NutrientListItem(
            label = { Text(stringResource(Res.string.unit_energy)) },
            value = {
                val value = facts.calories.value.formatClipZeros()
                val kcal = stringResource(Res.string.unit_kcal)
                Text(text = "$value $kcal")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_proteins),
                    color = nutrientsPalette.proteinsOnSurfaceContainer
                )
            },
            value = {
                val value = facts.proteins.value.formatClipZeros()
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_carbohydrates),
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer
                )
            },
            value = {
                val value = facts.carbohydrates.value.formatClipZeros()
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_sugars)) },
            value = {
                val summary = facts.sugars

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = {
                Text(
                    text = stringResource(Res.string.nutriment_fats),
                    color = nutrientsPalette.fatsOnSurfaceContainer
                )
            },
            value = {
                val value = facts.fats.value.formatClipZeros()
                Text(text = "$value $g")
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_saturated_fats)) },
            value = {
                val summary = facts.saturatedFats

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_monounsaturated_fats)) },
            value = {
                val summary = facts.monounsaturatedFats

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_polyunsaturated_fats)) },
            value = {
                val summary = facts.polyunsaturatedFats

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_omega_3)) },
            value = {
                val summary = facts.omega3

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_omega_6)) },
            value = {
                val summary = facts.omega6

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_salt)) },
            value = {
                val summary = facts.salt

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_fiber)) },
            value = {
                val summary = facts.fiber

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $g")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_cholesterol)) },
            value = {
                val summary = facts.cholesterolMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.nutriment_caffeine)) },
            value = {
                val summary = facts.caffeineMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_a)) },
            value = {
                val summary = facts.vitaminAMicro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b1)) },
            value = {
                val summary = facts.vitaminB1Milli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b2)) },
            value = {
                val summary = facts.vitaminB2Milli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b3)) },
            value = {
                val summary = facts.vitaminB3Milli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b5)) },
            value = {
                val summary = facts.vitaminB5Milli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b6)) },
            value = {
                val summary = facts.vitaminB6Milli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b9)) },
            value = {
                val summary = facts.vitaminB9Micro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_b12)) },
            value = {
                val summary = facts.vitaminB12Micro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_c)) },
            value = {
                val summary = facts.vitaminCMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_d)) },
            value = {
                val summary = facts.vitaminDMicro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_e)) },
            value = {
                val summary = facts.vitaminEMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.vitamin_k)) },
            value = {
                val summary = facts.vitaminKMicro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        HorizontalDivider()

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_manganese)) },
            value = {
                val summary = facts.manganeseMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_magnesium)) },
            value = {
                val summary = facts.magnesiumMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_potassium)) },
            value = {
                val summary = facts.potassiumMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_calcium)) },
            value = {
                val summary = facts.calciumMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_copper)) },
            value = {
                val summary = facts.copperMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_zinc)) },
            value = {
                val summary = facts.zincMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_sodium)) },
            value = {
                val summary = facts.sodiumMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_iron)) },
            value = {
                val summary = facts.ironMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_phosphorus)) },
            value = {
                val summary = facts.phosphorusMilli

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $mg")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_selenium)) },
            value = {
                val summary = facts.seleniumMicro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )

        NutrientListItem(
            label = { Text(stringResource(Res.string.mineral_iodine)) },
            value = {
                val summary = facts.iodineMicro

                when (summary) {
                    is NutrientValue.Incomplete -> incompleteValue(summary)()
                    is NutrientValue.Complete -> {
                        val value = summary.value.formatClipZeros()
                        Text("$value $ug")
                    }
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun NutrientListItem(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyLarge
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
}

object NutrientsListDefaults {
    val incompletePrefix: String
        @Composable get() = "*"

    fun incompleteValue(value: NutrientValue.Incomplete): @Composable () -> Unit = {
        val value = value.value?.formatClipZeros()

        val str = value?.let {
            "$incompletePrefix $it ${stringResource(Res.string.unit_gram_short)}"
        } ?: stringResource(Res.string.not_available_short)

        Text(
            text = str,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
