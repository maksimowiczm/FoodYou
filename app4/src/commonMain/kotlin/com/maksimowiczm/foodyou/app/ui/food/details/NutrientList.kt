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
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalEnergyFormatter
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.app.ui.common.utility.formatClipZeros
import com.maksimowiczm.foodyou.common.domain.NutritionFacts
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientList(facts: NutritionFacts, expanded: Boolean, modifier: Modifier = Modifier) {
    val nutrientsOrder = LocalNutrientsOrder.current

    Column(modifier = modifier) {
        val energy = facts.energy.value
        val energyText =
            if (energy != null) LocalEnergyFormatter.current.formatEnergy(energy)
            else stringResource(Res.string.not_available_short)

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
    val g = stringResource(Res.string.unit_gram_short)

    val amountText = amount.formatClipZeros() + " " + g

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
                val amountText = amount.formatClipZeros() + " " + g

                NutrientItem(
                    name = stringResource(Res.string.nutriment_fats),
                    amount = amountText,
                    color = nutrientsPalette.fatsOnSurfaceContainer.copy(alpha = .33f),
                )
            },
            expanded = expanded,
            modifier = modifier,
        ) {
            val saturatedAmount = facts.saturatedFats.value
            if (saturatedAmount != null) {
                val saturatedAmountText = saturatedAmount.formatClipZeros() + " " + g

                NutrientItem(
                    name = stringResource(Res.string.nutriment_saturated_fats),
                    amount = saturatedAmountText,
                )
            }

            val transAmount = facts.transFats.value
            if (transAmount != null) {
                val transAmountText = transAmount.formatClipZeros() + " " + g
                NutrientItem(
                    name = stringResource(Res.string.nutriment_trans_fats),
                    amount = transAmountText,
                )
            }

            val monounsaturatedAmount = facts.monounsaturatedFats.value
            if (monounsaturatedAmount != null) {
                val monounsaturatedAmountText = monounsaturatedAmount.formatClipZeros() + " " + g
                NutrientItem(
                    name = stringResource(Res.string.nutriment_monounsaturated_fats),
                    amount = monounsaturatedAmountText,
                )
            }

            val polyunsaturatedAmount = facts.polyunsaturatedFats.value
            val omega3Amount = facts.omega3.value
            val omega6Amount = facts.omega6.value

            if (polyunsaturatedAmount != null || omega3Amount != null || omega6Amount != null) {
                val polyunsaturatedAmountText =
                    if (polyunsaturatedAmount != null) {
                        polyunsaturatedAmount.formatClipZeros() + " " + g
                    } else {
                        stringResource(Res.string.not_available_short)
                    }

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
                        val omega3AmountText = omega3Amount.formatClipZeros() + " " + g
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_omega_3),
                            amount = omega3AmountText,
                        )
                    }

                    if (omega6Amount != null) {
                        val omega6AmountText = omega6Amount.formatClipZeros() + " " + g
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_omega_6),
                            amount = omega6AmountText,
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
    val g = stringResource(Res.string.unit_gram_short)

    val amountText = amount.formatClipZeros() + " " + g

    Column {
        NutrientGroup(
            title = {
                NutrientItem(
                    name = stringResource(Res.string.nutriment_carbohydrates),
                    amount = amountText,
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
                    if (sugarAmount != null) {
                        sugarAmount.formatClipZeros() + " " + g
                    } else {
                        stringResource(Res.string.not_available_short)
                    }

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
                        val addedSugarsText = addedSugars.formatClipZeros() + " " + g
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_added_sugars),
                            amount = addedSugarsText,
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
                    if (dietaryFiberAmount != null) {
                        dietaryFiberAmount.formatClipZeros() + " " + g
                    } else {
                        stringResource(Res.string.not_available_short)
                    }

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
                        val solubleFiberAmountText = solubleFiberAmount.formatClipZeros() + " " + g
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_soluble_fiber),
                            amount = solubleFiberAmountText,
                        )
                    }
                    if (insolubleFiberAmount != null) {
                        val insolubleFiberAmountText =
                            insolubleFiberAmount.formatClipZeros() + " " + g
                        NutrientItem(
                            name = stringResource(Res.string.nutriment_insoluble_fiber),
                            amount = insolubleFiberAmountText,
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
                    val saltText = salt.formatClipZeros() + " " + g
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_salt),
                        amount = saltText,
                    )
                }
                if (cholesterol != null) {
                    val cholesterol = cholesterol * 1_000
                    val cholesterolText = cholesterol.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_cholesterol),
                        amount = cholesterolText,
                    )
                }
                if (caffeine != null) {
                    val caffeine = caffeine * 1_000
                    val caffeineText = caffeine.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.nutriment_caffeine),
                        amount = caffeineText,
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
                    val vitaminA = vitaminA * 1_000_000
                    val vitaminAText = vitaminA.formatClipZeros() + " " + mcg
                    NutrientItem(name = stringResource(Res.string.vitamin_a), amount = vitaminAText)
                }
                if (vitaminB1 != null) {
                    val vitaminB1 = vitaminB1 * 1_000
                    val vitaminB1Text = vitaminB1.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b1),
                        amount = vitaminB1Text,
                    )
                }
                if (vitaminB2 != null) {
                    val vitaminB2 = vitaminB2 * 1_000
                    val vitaminB2Text = vitaminB2.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b2),
                        amount = vitaminB2Text,
                    )
                }
                if (vitaminB3 != null) {
                    val vitaminB3 = vitaminB3 * 1_000
                    val vitaminB3Text = vitaminB3.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b3),
                        amount = vitaminB3Text,
                    )
                }
                if (vitaminB5 != null) {
                    val vitaminB5 = vitaminB5 * 1_000
                    val vitaminB5Text = vitaminB5.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b5),
                        amount = vitaminB5Text,
                    )
                }
                if (vitaminB6 != null) {
                    val vitaminB6 = vitaminB6 * 1_000
                    val vitaminB6Text = vitaminB6.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b6),
                        amount = vitaminB6Text,
                    )
                }
                if (vitaminB7 != null) {
                    val vitaminB7 = vitaminB7 * 1_000_000
                    val vitaminB7Text = vitaminB7.formatClipZeros() + " " + mcg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b7),
                        amount = vitaminB7Text,
                    )
                }
                if (vitaminB9 != null) {
                    val vitaminB9 = vitaminB9 * 1_000_000
                    val vitaminB9Text = vitaminB9.formatClipZeros() + " " + mcg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b9),
                        amount = vitaminB9Text,
                    )
                }
                if (vitaminB12 != null) {
                    val vitaminB12 = vitaminB12 * 1_000_000
                    val vitaminB12Text = vitaminB12.formatClipZeros() + " " + mcg
                    NutrientItem(
                        name = stringResource(Res.string.vitamin_b12),
                        amount = vitaminB12Text,
                    )
                }
                if (vitaminC != null) {
                    val vitaminC = vitaminC * 1_000
                    val vitaminCText = vitaminC.formatClipZeros() + " " + mg
                    NutrientItem(name = stringResource(Res.string.vitamin_c), amount = vitaminCText)
                }
                if (vitaminD != null) {
                    val vitaminD = vitaminD * 1_000_000
                    val vitaminDText = vitaminD.formatClipZeros() + " " + mcg
                    NutrientItem(name = stringResource(Res.string.vitamin_d), amount = vitaminDText)
                }
                if (vitaminE != null) {
                    val vitaminE = vitaminE * 1_000
                    val vitaminEText = vitaminE.formatClipZeros() + " " + mg
                    NutrientItem(name = stringResource(Res.string.vitamin_e), amount = vitaminEText)
                }
                if (vitaminK != null) {
                    val vitaminK = vitaminK * 1_000_000
                    val vitaminKText = vitaminK.formatClipZeros() + " " + mcg
                    NutrientItem(name = stringResource(Res.string.vitamin_k), amount = vitaminKText)
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
                    val manganese = manganese * 1_000
                    val manganeseText = manganese.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_manganese),
                        amount = manganeseText,
                    )
                }
                if (magnesium != null) {
                    val magnesium = magnesium * 1_000
                    val magnesiumText = magnesium.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_magnesium),
                        amount = magnesiumText,
                    )
                }
                if (potassium != null) {
                    val potassium = potassium * 1_000
                    val potassiumText = potassium.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_potassium),
                        amount = potassiumText,
                    )
                }
                if (calcium != null) {
                    val calcium = calcium * 1_000
                    val calciumText = calcium.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_calcium),
                        amount = calciumText,
                    )
                }
                if (copper != null) {
                    val copper = copper * 1_000
                    val copperText = copper.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_copper),
                        amount = copperText,
                    )
                }
                if (zinc != null) {
                    val zinc = zinc * 1_000
                    val zincText = zinc.formatClipZeros() + " " + mg
                    NutrientItem(name = stringResource(Res.string.mineral_zinc), amount = zincText)
                }
                if (sodium != null) {
                    val sodium = sodium * 1_000
                    val sodiumText = sodium.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_sodium),
                        amount = sodiumText,
                    )
                }
                if (iron != null) {
                    val iron = iron * 1_000
                    val ironText = iron.formatClipZeros() + " " + mg
                    NutrientItem(name = stringResource(Res.string.mineral_iron), amount = ironText)
                }
                if (phosphorus != null) {
                    val phosphorus = phosphorus * 1_000
                    val phosphorusText = phosphorus.formatClipZeros() + " " + mg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_phosphorus),
                        amount = phosphorusText,
                    )
                }
                if (selenium != null) {
                    val selenium = selenium * 1_000_000
                    val seleniumText = selenium.formatClipZeros() + " " + mcg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_selenium),
                        amount = seleniumText,
                    )
                }
                if (chromium != null) {
                    val chromium = chromium * 1_000_000
                    val chromiumText = chromium.formatClipZeros() + " " + mcg
                    NutrientItem(
                        name = stringResource(Res.string.mineral_chromium),
                        amount = chromiumText,
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
