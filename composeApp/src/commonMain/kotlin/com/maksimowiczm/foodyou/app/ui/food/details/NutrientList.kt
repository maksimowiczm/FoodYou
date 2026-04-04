package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.app.ui.common.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.common.domain.Div
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.Plus
import com.maksimowiczm.foodyou.common.domain.Times
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NutrientList(facts: NutritionFacts, expanded: Boolean, modifier: Modifier = Modifier) {
    val nutrientsOrder = LocalNutrientsOrder.current

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
        Column(modifier) {
            NutrientItem(
                name = stringResource(Res.string.unit_energy),
                amount = facts.energy,
                unit = LocalEnergyUnit.current,
                amountColor =
                    if (facts.energy.value != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            nutrientsOrder.forEachIndexed { i, order ->
                val isLast = i == nutrientsOrder.lastIndex
                val bottomSpacer: @Composable () -> Unit =
                    if (isLast) {
                        {}
                    } else {
                        { Spacer(Modifier.height(8.dp)) }
                    }
                when (order) {
                    NutrientsOrder.Proteins -> Proteins(facts) { bottomSpacer() }

                    NutrientsOrder.Fats -> Fats(facts, expanded) { bottomSpacer() }

                    NutrientsOrder.Carbohydrates ->
                        Carbohydrates(facts, expanded) { bottomSpacer() }

                    NutrientsOrder.Other -> Other(facts, expanded) { bottomSpacer() }

                    NutrientsOrder.Vitamins -> Vitamins(facts, expanded) { bottomSpacer() }

                    NutrientsOrder.Minerals -> Minerals(facts, expanded) { bottomSpacer() }
                }
            }
        }
    }
}

@Composable
private fun Proteins(
    facts: NutritionFacts,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    if (facts.proteins.value == null) return

    Column {
        NutrientItem(
            name = stringResource(Res.string.nutriment_proteins),
            amount = facts.proteins,
            color = nutrientsPalette.proteinsOnSurfaceContainer.copy(alpha = .33f),
            modifier = modifier,
        )
        bottomSpacer()
    }
}

@Composable
private fun Fats(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val anyPresent =
        sequenceOf(
                facts.fats.value != null,
                facts.sugars.value != null,
                facts.addedSugars.value != null,
                facts.dietaryFiber.value != null,
                facts.solubleFiber.value != null,
                facts.insolubleFiber.value != null,
            )
            .any { it }

    if (!anyPresent) return

    Column {
        NutrientGroup(
            title = {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_fats),
                    amount = facts.fats,
                    color = nutrientsPalette.fatsOnSurfaceContainer.copy(alpha = .33f),
                )
            },
            expanded = expanded,
            modifier = modifier,
        ) {
            if (facts.saturatedFats.value != null) {
                NutrientItem(
                    amount = facts.saturatedFats,
                    name = stringResource(Res.string.nutriment_saturated_fats),
                )
            }
            if (facts.transFats.value != null) {
                NutrientItem(
                    amount = facts.transFats,
                    name = stringResource(Res.string.nutriment_trans_fats),
                )
            }
            if (facts.monounsaturatedFats.value != null) {
                NutrientItem(
                    amount = facts.monounsaturatedFats,
                    name = stringResource(Res.string.nutriment_monounsaturated_fats),
                )
            }
            if (
                facts.polyunsaturatedFats.value != null ||
                    facts.omega3.value != null ||
                    facts.omega6.value != null
            ) {
                NutrientGroup(
                    title = {
                        NutrientItem(
                            amount = facts.polyunsaturatedFats,
                            name = stringResource(Res.string.nutriment_polyunsaturated_fats),
                            amountColor =
                                if (facts.polyunsaturatedFats.value != null)
                                    MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (facts.omega3.value != null) {
                        NutrientItem(
                            amount = facts.omega3,
                            name = stringResource(Res.string.nutriment_omega_3),
                        )
                    }
                    if (facts.omega6.value != null) {
                        NutrientItem(
                            amount = facts.omega6,
                            name = stringResource(Res.string.nutriment_omega_6),
                        )
                    }
                }
            }
        }
        bottomSpacer()
    }
}

@Composable
private fun Carbohydrates(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val anyPresent =
        sequenceOf(
                facts.carbohydrates.value != null,
                facts.sugars.value != null,
                facts.addedSugars.value != null,
                facts.dietaryFiber.value != null,
                facts.solubleFiber.value != null,
                facts.insolubleFiber.value != null,
            )
            .any { it }

    if (!anyPresent) return

    Column {
        NutrientGroup(
            title = {
                NutrientItem(
                    amount = facts.carbohydrates,
                    name = stringResource(Res.string.nutriment_carbohydrates),
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer.copy(alpha = .33f),
                )
            },
            modifier = modifier,
            expanded = expanded,
        ) {
            if (facts.sugars.value != null || facts.addedSugars.value != null) {
                NutrientGroup(
                    title = {
                        NutrientItem(
                            amount = facts.sugars,
                            name = stringResource(Res.string.nutriment_sugars),
                            amountColor =
                                if (facts.sugars.value != null) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (facts.addedSugars.value != null) {
                        NutrientItem(
                            amount = facts.addedSugars,
                            name = stringResource(Res.string.nutriment_added_sugars),
                        )
                    }
                }
            }
            if (
                facts.dietaryFiber.value != null ||
                    facts.solubleFiber.value != null ||
                    facts.insolubleFiber.value != null
            ) {
                NutrientGroup(
                    title = {
                        NutrientItem(
                            amount = facts.dietaryFiber,
                            name = stringResource(Res.string.nutriment_fiber),
                            amountColor =
                                if (facts.dietaryFiber.value != null)
                                    MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (facts.solubleFiber.value != null) {
                        NutrientItem(
                            amount = facts.solubleFiber,
                            name = stringResource(Res.string.nutriment_soluble_fiber),
                        )
                    }
                    if (facts.insolubleFiber.value != null) {
                        NutrientItem(
                            amount = facts.insolubleFiber,
                            name = stringResource(Res.string.nutriment_insoluble_fiber),
                        )
                    }
                }
            }
        }
        bottomSpacer()
    }
}

@Composable
private fun Other(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val anyPresent =
        sequenceOf(
                facts.salt.value != null,
                facts.cholesterol.value != null,
                facts.caffeine.value != null,
            )
            .any { it }

    if (!anyPresent) return

    NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
        Column {
            Text(
                text = stringResource(Res.string.headline_other),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            if (facts.salt.value != null) {
                NutrientItem(amount = facts.salt, name = stringResource(Res.string.nutriment_salt))
            }
            if (facts.cholesterol.value != null) {
                NutrientItem(
                    amount = facts.cholesterol.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.nutriment_cholesterol),
                )
            }
            if (facts.caffeine.value != null) {
                NutrientItem(
                    amount = facts.caffeine.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.nutriment_caffeine),
                )
            }
            bottomSpacer()
        }
    }
}

@Composable
private fun Vitamins(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val anyPresent =
        sequenceOf(
                facts.vitaminA.value != null,
                facts.vitaminB1.value != null,
                facts.vitaminB2.value != null,
                facts.vitaminB3.value != null,
                facts.vitaminB5.value != null,
                facts.vitaminB6.value != null,
                facts.vitaminB7.value != null,
                facts.vitaminB9.value != null,
                facts.vitaminB12.value != null,
                facts.vitaminC.value != null,
                facts.vitaminD.value != null,
                facts.vitaminE.value != null,
                facts.vitaminK.value != null,
            )
            .any { it }

    if (!anyPresent) return

    NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
        Column {
            Text(
                text = stringResource(Res.string.headline_vitamins),
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            if (facts.vitaminA.value != null) {
                NutrientItem(
                    amount = facts.vitaminA.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_a),
                )
            }
            if (facts.vitaminB1.value != null) {
                NutrientItem(
                    amount = facts.vitaminB1.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_b1),
                )
            }
            if (facts.vitaminB2.value != null) {
                NutrientItem(
                    amount = facts.vitaminB2.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_b2),
                )
            }
            if (facts.vitaminB3.value != null) {
                NutrientItem(
                    amount = facts.vitaminB3.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_b3),
                )
            }
            if (facts.vitaminB5.value != null) {
                NutrientItem(
                    amount = facts.vitaminB5.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_b5),
                )
            }
            if (facts.vitaminB6.value != null) {
                NutrientItem(
                    amount = facts.vitaminB6.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_b6),
                )
            }
            if (facts.vitaminB7.value != null) {
                NutrientItem(
                    amount = facts.vitaminB7.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_b7),
                )
            }
            if (facts.vitaminB9.value != null) {
                NutrientItem(
                    amount = facts.vitaminB9.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_b9),
                )
            }
            if (facts.vitaminB12.value != null) {
                NutrientItem(
                    amount = facts.vitaminB12.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_b12),
                )
            }
            if (facts.vitaminC.value != null) {
                NutrientItem(
                    amount = facts.vitaminC.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_c),
                )
            }
            if (facts.vitaminD.value != null) {
                NutrientItem(
                    amount = facts.vitaminD.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_d),
                )
            }
            if (facts.vitaminE.value != null) {
                NutrientItem(
                    amount = facts.vitaminE.inUnit(WeightUnit.Milligrams),
                    name = stringResource(Res.string.vitamin_e),
                )
            }
            if (facts.vitaminK.value != null) {
                NutrientItem(
                    amount = facts.vitaminK.inUnit(WeightUnit.Micrograms),
                    name = stringResource(Res.string.vitamin_k),
                )
            }
            bottomSpacer()
        }
    }
}

@Composable
private fun Minerals(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    bottomSpacer: @Composable () -> Unit = { Spacer(Modifier.height(8.dp)) },
) {
    val anyMinerals =
        listOf(
                facts.manganese.value != null,
                facts.magnesium.value != null,
                facts.potassium.value != null,
                facts.calcium.value != null,
                facts.copper.value != null,
                facts.zinc.value != null,
                facts.sodium.value != null,
                facts.iron.value != null,
                facts.phosphorus.value != null,
                facts.selenium.value != null,
                facts.iodine.value != null,
                facts.chromium.value != null,
            )
            .any { it }

    if (anyMinerals) {
        NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
            Column {
                Text(
                    text = stringResource(Res.string.headline_minerals),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (facts.manganese.value != null) {
                    NutrientItem(
                        amount = facts.manganese.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_manganese),
                    )
                }
                if (facts.magnesium.value != null) {
                    NutrientItem(
                        amount = facts.magnesium.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_magnesium),
                    )
                }
                if (facts.potassium.value != null) {
                    NutrientItem(
                        amount = facts.potassium.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_potassium),
                    )
                }
                if (facts.calcium.value != null) {
                    NutrientItem(
                        amount = facts.calcium.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_calcium),
                    )
                }
                if (facts.copper.value != null) {
                    NutrientItem(
                        amount = facts.copper.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_copper),
                    )
                }
                if (facts.zinc.value != null) {
                    NutrientItem(
                        amount = facts.zinc.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_zinc),
                    )
                }
                if (facts.sodium.value != null) {
                    NutrientItem(
                        amount = facts.sodium.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_sodium),
                    )
                }
                if (facts.iron.value != null) {
                    NutrientItem(
                        amount = facts.iron.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_iron),
                    )
                }
                if (facts.phosphorus.value != null) {
                    NutrientItem(
                        amount = facts.phosphorus.inUnit(WeightUnit.Milligrams),
                        name = stringResource(Res.string.mineral_phosphorus),
                    )
                }
                if (facts.selenium.value != null) {
                    NutrientItem(
                        amount = facts.selenium.inUnit(WeightUnit.Micrograms),
                        name = stringResource(Res.string.mineral_selenium),
                    )
                }
                if (facts.iodine.value != null) {
                    NutrientItem(
                        amount = facts.iodine.inUnit(WeightUnit.Micrograms),
                        name = stringResource(Res.string.mineral_iodine),
                    )
                }
                if (facts.chromium.value != null) {
                    NutrientItem(
                        amount = facts.chromium.inUnit(WeightUnit.Micrograms),
                        name = stringResource(Res.string.mineral_chromium),
                    )
                }
                bottomSpacer()
            }
        }
    }
}

@Composable
private fun NutrientGroup(
    title: @Composable () -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        title()
        NutrientAnimatedVisibility(expanded) {
            Column(modifier = Modifier.padding(start = 16.dp)) { content() }
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
private fun NutrientItem(
    name: String,
    amount: NutrientValue<Weight>,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    amountColor: Color = LocalContentColor.current,
) {
    Surface(modifier = modifier, color = color, shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = name)
            Spacer(Modifier.weight(1f).widthIn(min = 8.dp))
            when (amount) {
                is NutrientValue.Complete<Weight> ->
                    Text(text = amount.value.stringResource(), color = amountColor)

                is NutrientValue.Incomplete<Weight> -> IncompleteValue(amount)
            }
        }
    }
}

@Composable
private fun NutrientItem(
    name: String,
    amount: NutrientValue<Energy>,
    unit: EnergyUnit,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    amountColor: Color = LocalContentColor.current,
) {
    Surface(modifier = modifier, color = color, shape = MaterialTheme.shapes.small) {
        Row(Modifier.padding(8.dp)) {
            Text(name)
            Spacer(Modifier.weight(1f).widthIn(min = 8.dp))
            when (amount) {
                is NutrientValue.Complete<Energy> ->
                    Text(text = amount.value.inUnit(unit).stringResource(), color = amountColor)

                is NutrientValue.Incomplete<Energy> -> {
                    val amountStr = amount.value?.inUnit(unit)?.stringResource()
                    val str =
                        if (amountStr != null) "$INCOMPLETE_PREFIX $amountStr"
                        else stringResource(Res.string.not_available_short)
                    Text(text = str, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun NutrientAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val motionScheme = MaterialTheme.motionScheme

    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter =
            fadeIn(motionScheme.defaultEffectsSpec()) +
                expandVertically(motionScheme.slowSpatialSpec()),
        exit =
            fadeOut(motionScheme.defaultEffectsSpec()) +
                shrinkVertically(motionScheme.slowSpatialSpec()),
    ) {
        content()
    }
}

@Composable
private fun NutrientValue<Weight>.inUnit(unit: WeightUnit): NutrientValue<Weight> =
    remember(this) { map { it?.inUnit(unit) } }

private fun <T> NutrientValue<T>.map(transform: (T?) -> T?): NutrientValue<T>
    where T : Plus<T>, T : Times<T>, T : Div<T> =
    when (this) {
        is NutrientValue.Complete<*> -> NutrientValue.Complete(transform(value)!!)
        is NutrientValue.Incomplete<*> -> NutrientValue.Incomplete(transform(value))
    }

private const val INCOMPLETE_PREFIX: String = "*"

@Composable
private fun IncompleteValue(
    value: NutrientValue.Incomplete<Weight>,
    modifier: Modifier = Modifier,
) {
    val amountStr = value.value?.stringResource()
    val str =
        if (amountStr != null) "$INCOMPLETE_PREFIX $amountStr"
        else stringResource(Res.string.not_available_short)
    Text(
        text = str,
        modifier = modifier,
        color = MaterialTheme.colorScheme.outline,
        style = MaterialTheme.typography.bodyMedium,
    )
}
