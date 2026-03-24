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
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NutrientList(facts: NutritionFacts, expanded: Boolean, modifier: Modifier = Modifier) {
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier) {
        val energy = facts.energy.value
        val energyText =
            energy?.inUnit(LocalEnergyUnit.current)?.stringResource()
                ?: stringResource(Res.string.not_available_short)

        NutrientItem(
            name = stringResource(Res.string.unit_energy),
            amount = energyText,
            amountColor =
                if (energy != null) MaterialTheme.colorScheme.onSurface
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
                NutrientsOrder.Carbohydrates -> Carbohydrates(facts, expanded) { bottomSpacer() }
                NutrientsOrder.Other -> Other(facts, expanded) { bottomSpacer() }
                NutrientsOrder.Vitamins -> Vitamins(facts, expanded) { bottomSpacer() }
                NutrientsOrder.Minerals -> Minerals(facts, expanded) { bottomSpacer() }
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

    val amount = facts.proteins.value ?: return
    val amountText = amount.stringResource()

    Column {
        NutrientItem(
            name = stringResource(Res.string.nutriment_proteins),
            amount = amountText,
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
    val amount = facts.fats.value ?: return

    val g = stringResource(Res.string.unit_gram_short)

    Column {
        NutrientGroup(
            title = {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_fats),
                    amount = amount.stringResource(),
                    color = nutrientsPalette.fatsOnSurfaceContainer.copy(alpha = .33f),
                )
            },
            expanded = expanded,
            modifier = modifier,
        ) {
            val saturatedAmount = facts.saturatedFats.value
            if (saturatedAmount != null) {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_saturated_fats),
                    amount = saturatedAmount.stringResource(),
                )
            }

            val transAmount = facts.transFats.value
            if (transAmount != null) {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_trans_fats),
                    amount = transAmount.stringResource(),
                )
            }

            val monounsaturatedAmount = facts.monounsaturatedFats.value
            if (monounsaturatedAmount != null) {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_monounsaturated_fats),
                    amount = monounsaturatedAmount.stringResource(),
                )
            }

            val polyunsaturatedAmount = facts.polyunsaturatedFats.value
            val omega3Amount = facts.omega3.value
            val omega6Amount = facts.omega6.value

            if (polyunsaturatedAmount != null || omega3Amount != null || omega6Amount != null) {
                val polyunsaturatedAmountText =
                    polyunsaturatedAmount?.stringResource()
                        ?: stringResource(Res.string.not_available_short)

                NutrientGroup(
                    title = {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_polyunsaturated_fats),
                            amount = polyunsaturatedAmountText,
                            amountColor =
                                if (polyunsaturatedAmount != null)
                                    MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (omega3Amount != null) {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_omega_3),
                            amount = omega3Amount.stringResource(),
                        )
                    }

                    if (omega6Amount != null) {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_omega_6),
                            amount = omega6Amount.stringResource(),
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

    val amount = facts.carbohydrates.value ?: return
    Column {
        NutrientGroup(
            title = {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_carbohydrates),
                    amount = amount.stringResource(),
                    color = nutrientsPalette.carbohydratesOnSurfaceContainer.copy(alpha = .33f),
                )
            },
            modifier = modifier,
            expanded = expanded,
        ) {
            val sugarAmount = facts.sugars.value
            val addedSugars = facts.addedSugars.value
            if (sugarAmount != null || addedSugars != null) {
                val sugarAmountText =
                    sugarAmount?.stringResource() ?: stringResource(Res.string.not_available_short)

                NutrientGroup(
                    title = {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_sugars),
                            amount = sugarAmountText,
                            amountColor =
                                if (sugarAmount != null) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (addedSugars != null) {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_added_sugars),
                            amount = addedSugars.stringResource(),
                        )
                    }
                }
            }

            val dietaryFiberAmount = facts.dietaryFiber.value
            val solubleFiberAmount = facts.solubleFiber.value
            val insolubleFiberAmount = facts.insolubleFiber.value
            if (
                dietaryFiberAmount != null ||
                    solubleFiberAmount != null ||
                    insolubleFiberAmount != null
            ) {
                val dietaryFiberAmountText =
                    dietaryFiberAmount?.stringResource()
                        ?: stringResource(Res.string.not_available_short)

                NutrientGroup(
                    title = {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_fiber),
                            amount = dietaryFiberAmountText,
                            amountColor =
                                if (dietaryFiberAmount != null) MaterialTheme.colorScheme.onSurface
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                ) {
                    if (solubleFiberAmount != null) {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_soluble_fiber),
                            amount = solubleFiberAmount.stringResource(),
                        )
                    }
                    if (insolubleFiberAmount != null) {
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_insoluble_fiber),
                            amount = insolubleFiberAmount.stringResource(),
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
    val salt = facts.salt.value
    val cholesterol = facts.cholesterol.value
    val caffeine = facts.caffeine.value

    if (salt != null || cholesterol != null || caffeine != null) {
        val g = stringResource(Res.string.unit_gram_short)
        val mg = stringResource(Res.string.unit_milligram_short)

        NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
            Column {
                Text(
                    text = stringResource(Res.string.headline_other),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (salt != null) {
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_salt),
                        amount = salt.stringResource(),
                    )
                }
                if (cholesterol != null) {
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_cholesterol),
                        amount = cholesterol.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (caffeine != null) {
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_caffeine),
                        amount = caffeine.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                bottomSpacer()
            }
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
    val vitaminA = facts.vitaminA.value
    val vitaminB1 = facts.vitaminB1.value
    val vitaminB2 = facts.vitaminB2.value
    val vitaminB3 = facts.vitaminB3.value
    val vitaminB5 = facts.vitaminB5.value
    val vitaminB6 = facts.vitaminB6.value
    val vitaminB7 = facts.vitaminB7.value
    val vitaminB9 = facts.vitaminB9.value
    val vitaminB12 = facts.vitaminB12.value
    val vitaminC = facts.vitaminC.value
    val vitaminD = facts.vitaminD.value
    val vitaminE = facts.vitaminE.value
    val vitaminK = facts.vitaminK.value

    val anyVitamins =
        listOf(
                vitaminA,
                vitaminB1,
                vitaminB2,
                vitaminB3,
                vitaminB5,
                vitaminB6,
                vitaminB7,
                vitaminB9,
                vitaminB12,
                vitaminC,
                vitaminD,
                vitaminE,
                vitaminK,
            )
            .any { it != null }

    if (anyVitamins) {
        val mg = stringResource(Res.string.unit_milligram_short)
        val mcg = stringResource(Res.string.unit_microgram_short)

        NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
            Column {
                Text(
                    text = stringResource(Res.string.headline_vitamins),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )

                if (vitaminA != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_a),
                        amount = vitaminA.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (vitaminB1 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b1),
                        amount = vitaminB1.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminB2 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b2),
                        amount = vitaminB2.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminB3 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b3),
                        amount = vitaminB3.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminB5 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b5),
                        amount = vitaminB5.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminB6 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b6),
                        amount = vitaminB6.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminB7 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b7),
                        amount = vitaminB7.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (vitaminB9 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b9),
                        amount = vitaminB9.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (vitaminB12 != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b12),
                        amount = vitaminB12.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (vitaminC != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_c),
                        amount = vitaminC.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminD != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_d),
                        amount = vitaminD.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (vitaminE != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_e),
                        amount = vitaminE.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (vitaminK != null) {
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_k),
                        amount = vitaminK.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                bottomSpacer()
            }
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
    val manganese = facts.manganese.value
    val magnesium = facts.magnesium.value
    val potassium = facts.potassium.value
    val calcium = facts.calcium.value
    val copper = facts.copper.value
    val zinc = facts.zinc.value
    val sodium = facts.sodium.value
    val iron = facts.iron.value
    val phosphorus = facts.phosphorus.value
    val selenium = facts.selenium.value
    val chromium = facts.chromium.value

    val anyMinerals =
        listOf(
                manganese,
                magnesium,
                potassium,
                calcium,
                copper,
                zinc,
                sodium,
                iron,
                phosphorus,
                selenium,
                chromium,
            )
            .any { it != null }

    if (anyMinerals) {
        val mg = stringResource(Res.string.unit_milligram_short)
        val mcg = stringResource(Res.string.unit_microgram_short)

        NutrientAnimatedVisibility(visible = expanded, modifier = modifier) {
            Column {
                Text(
                    text = stringResource(Res.string.headline_minerals),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (manganese != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_manganese),
                        amount = manganese.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (magnesium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_magnesium),
                        amount = magnesium.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (potassium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_potassium),
                        amount = potassium.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (calcium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_calcium),
                        amount = calcium.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (copper != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_copper),
                        amount = copper.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (zinc != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_zinc),
                        amount = zinc.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (sodium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_sodium),
                        amount = sodium.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (iron != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_iron),
                        amount = iron.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (phosphorus != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_phosphorus),
                        amount = phosphorus.inUnit(WeightUnit.Milligrams).stringResource(),
                    )
                }
                if (selenium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_selenium),
                        amount = selenium.inUnit(WeightUnit.Micrograms).stringResource(),
                    )
                }
                if (chromium != null) {
                    NutrientItem(
                        name = stringResource(Res.string.mineral_chromium),
                        amount = chromium.inUnit(WeightUnit.Micrograms).stringResource(),
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
    amount: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    amountColor: Color = LocalContentColor.current,
) {
    Surface(modifier = modifier, color = color, shape = MaterialTheme.shapes.small) {
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = name, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.weight(1f).widthIn(min = 8.dp))
            Text(text = amount, style = MaterialTheme.typography.bodyMedium, color = amountColor)
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
