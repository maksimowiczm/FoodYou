package com.maksimowiczm.foodyou.capabilities.food

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.common.domain.Energy
import com.maksimowiczm.foodyou.common.domain.Weight
import com.maksimowiczm.foodyou.common.domain.WeightUnit
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.isIncomplete
import com.maksimowiczm.foodyou.common.domain.food.map
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.kilocalories
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource
import foodyou.app.generated.resources.*
import kotlin.jvm.JvmName
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientList(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val nutrientsOrder = LocalNutrientsOrder.current

    val spacerHeight =
        animateDpAsState(
            if (expanded) 8.dp else 2.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    Box(modifier) {
        Column(Modifier.clip(MaterialTheme.shapes.large)) {
            Energy(
                facts = facts,
                expanded = expanded,
                modifier = Modifier.fillMaxWidth(),
            )
            nutrientsOrder.forEach { order ->
                val isCoreNutrient =
                    order == NutrientsOrder.Proteins ||
                        order == NutrientsOrder.Fats ||
                        order == NutrientsOrder.Carbohydrates
                val isAvailable =
                    when (order) {
                        NutrientsOrder.Proteins -> facts.hasProteins()
                        NutrientsOrder.Fats -> facts.hasAnyFatsComponent()
                        NutrientsOrder.Carbohydrates -> facts.hasAnyCarbohydratesComponent()
                        NutrientsOrder.Other -> facts.hasAnyOtherComponent()
                        NutrientsOrder.Vitamins -> facts.hasAnyVitamins()
                        NutrientsOrder.Minerals -> facts.hasAnyMinerals()
                    }

                AnimatedVisibility(
                    visible = isAvailable && (expanded || isCoreNutrient),
                    enter =
                        fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
                            expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
                    exit =
                        fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                            shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()),
                ) {
                    Column {
                        Spacer(Modifier.height(spacerHeight.value))
                        when (order) {
                            NutrientsOrder.Proteins -> Proteins(facts = facts, expanded = expanded)
                            NutrientsOrder.Fats -> Fats(facts = facts, expanded = expanded)
                            NutrientsOrder.Carbohydrates ->
                                Carbohydrates(facts = facts, expanded = expanded)

                            NutrientsOrder.Other -> Other(facts = facts, expanded = expanded)
                            NutrientsOrder.Vitamins -> Vitamins(facts = facts, expanded = expanded)
                            NutrientsOrder.Minerals -> Minerals(facts = facts, expanded = expanded)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Energy(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    NutrientHolder(
        label = { Text(stringResource(Res.string.unit_energy)) },
        value = {
            Text(
                facts.energy
                    .map {
                        it.inUnit(LocalEnergyUnit.current)
                    }
                    .stringResource(),
                color = facts.energy.color(),
            )
        },
        color = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.extraSmall,
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            },
    )
}

@Composable
private fun Proteins(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )

    NutrientHolder(
        label = { Text(stringResource(Res.string.nutriment_proteins)) },
        value = { Text(facts.proteins.stringResource(), color = facts.proteins.color()) },
        color = nutrientsPalette.proteinsOnSurfaceContainer.copy(alpha = .1f),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.extraSmall,
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            },
    )
}

@Composable
private fun Fats(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )
    val color = nutrientsPalette.fatsOnSurfaceContainer.copy(alpha = .1f)

    Column(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            }
    ) {
        val showOmega3 = facts.omega3.value != null
        val showOmega6 = facts.omega6.value != null
        val showPolyunsaturatedFats =
            facts.polyunsaturatedFats.value != null || showOmega3 || showOmega6
        val showSaturatedFats = facts.saturatedFats.value != null
        val showTransFats = facts.transFats.value != null
        val showMonounsaturatedFats = facts.monounsaturatedFats.value != null
        val showFats =
            facts.fats.value != null ||
                showSaturatedFats ||
                showTransFats ||
                showMonounsaturatedFats ||
                showPolyunsaturatedFats

        if (showFats) {
            NutrientHolder(
                label = { Text(stringResource(Res.string.nutriment_fats)) },
                value = { Text(facts.fats.stringResource(), color = facts.fats.color()) },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column {
                if (showSaturatedFats) {
                    Spacer(Modifier.height(2.dp))
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_saturated_fats),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.saturatedFats.stringResource(),
                                color = facts.saturatedFats.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showTransFats) {
                    if (showSaturatedFats || showFats) Spacer(Modifier.height(2.dp))
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_trans_fats),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(facts.transFats.stringResource(), color = facts.transFats.color())
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showMonounsaturatedFats) {
                    if (showSaturatedFats || showTransFats || showFats)
                        Spacer(Modifier.height(2.dp))
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_monounsaturated_fats),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.monounsaturatedFats.stringResource(),
                                color = facts.monounsaturatedFats.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showPolyunsaturatedFats) {
                    if (showSaturatedFats || showTransFats || showMonounsaturatedFats || showFats) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_polyunsaturated_fats),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.polyunsaturatedFats.stringResource(),
                                color = facts.polyunsaturatedFats.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showOmega3) {
                    if (
                        showSaturatedFats ||
                            showTransFats ||
                            showMonounsaturatedFats ||
                            showPolyunsaturatedFats ||
                            showFats
                    ) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_omega_3),
                                Modifier.padding(start = 16.dp),
                            )
                        },
                        value = {
                            Text(facts.omega3.stringResource(), color = facts.omega3.color())
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showOmega6) {
                    if (
                        showSaturatedFats ||
                            showTransFats ||
                            showMonounsaturatedFats ||
                            showPolyunsaturatedFats ||
                            showOmega3 ||
                            showFats
                    ) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_omega_6),
                                Modifier.padding(start = 16.dp),
                            )
                        },
                        value = {
                            Text(facts.omega6.stringResource(), color = facts.omega6.color())
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
            }
        }
    }
}

@Composable
private fun Other(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )
    val color = MaterialTheme.colorScheme.surfaceContainer

    Column(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            }
    ) {
        if (facts.salt.value != null) {
            NutrientHolder(
                label = { Text(stringResource(Res.string.nutriment_salt)) },
                value = { Text(facts.salt.stringResource(), color = facts.salt.color()) },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.cholesterol.value != null) {
            if (facts.salt.value != null) Spacer(Modifier.height(2.dp))
            NutrientHolder(
                label = { Text(stringResource(Res.string.nutriment_cholesterol)) },
                value = {
                    Text(
                        facts.cholesterol.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.cholesterol.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.caffeine.value != null) {
            if (facts.salt.value != null || facts.cholesterol.value != null) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.nutriment_caffeine)) },
                value = {
                    Text(
                        facts.caffeine.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.caffeine.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
    }
}

@Composable
private fun Vitamins(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )
    val color = MaterialTheme.colorScheme.surfaceContainer

    Column(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            }
    ) {
        if (facts.vitaminA.value != null) {
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_a)) },
                value = {
                    Text(
                        facts.vitaminA.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminA.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB1.value != null) {
            if (facts.vitaminA.value != null) Spacer(Modifier.height(2.dp))
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b1)) },
                value = {
                    Text(
                        facts.vitaminB1.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminB1.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB2.value != null) {
            if (facts.vitaminA.value != null || facts.vitaminB1.value != null) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b2)) },
                value = {
                    Text(
                        facts.vitaminB2.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminB2.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB3.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b3)) },
                value = {
                    Text(
                        facts.vitaminB3.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminB3.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB5.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b5)) },
                value = {
                    Text(
                        facts.vitaminB5.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminB5.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB6.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b6)) },
                value = {
                    Text(
                        facts.vitaminB6.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminB6.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB7.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b7)) },
                value = {
                    Text(
                        facts.vitaminB7.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminB7.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB9.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b9)) },
                value = {
                    Text(
                        facts.vitaminB9.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminB9.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminB12.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null ||
                    facts.vitaminB9.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_b12)) },
                value = {
                    Text(
                        facts.vitaminB12.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminB12.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminC.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null ||
                    facts.vitaminB9.value != null ||
                    facts.vitaminB12.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_c)) },
                value = {
                    Text(
                        facts.vitaminC.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminC.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminD.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null ||
                    facts.vitaminB9.value != null ||
                    facts.vitaminB12.value != null ||
                    facts.vitaminC.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_d)) },
                value = {
                    Text(
                        facts.vitaminD.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminD.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminE.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null ||
                    facts.vitaminB9.value != null ||
                    facts.vitaminB12.value != null ||
                    facts.vitaminC.value != null ||
                    facts.vitaminD.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_e)) },
                value = {
                    Text(
                        facts.vitaminE.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.vitaminE.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.vitaminK.value != null) {
            if (
                facts.vitaminA.value != null ||
                    facts.vitaminB1.value != null ||
                    facts.vitaminB2.value != null ||
                    facts.vitaminB3.value != null ||
                    facts.vitaminB5.value != null ||
                    facts.vitaminB6.value != null ||
                    facts.vitaminB7.value != null ||
                    facts.vitaminB9.value != null ||
                    facts.vitaminB12.value != null ||
                    facts.vitaminC.value != null ||
                    facts.vitaminD.value != null ||
                    facts.vitaminE.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.vitamin_k)) },
                value = {
                    Text(
                        facts.vitaminK.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.vitaminK.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
    }
}

@Composable
private fun Minerals(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )
    val color = MaterialTheme.colorScheme.surfaceContainer

    Column(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            }
    ) {
        if (facts.manganese.value != null) {
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_manganese)) },
                value = {
                    Text(
                        facts.manganese.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.manganese.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.magnesium.value != null) {
            if (facts.manganese.value != null) Spacer(Modifier.height(2.dp))
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_magnesium)) },
                value = {
                    Text(
                        facts.magnesium.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.magnesium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.potassium.value != null) {
            if (facts.manganese.value != null || facts.magnesium.value != null) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_potassium)) },
                value = {
                    Text(
                        facts.potassium.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.potassium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.calcium.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_calcium)) },
                value = {
                    Text(
                        facts.calcium.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.calcium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.copper.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_copper)) },
                value = {
                    Text(
                        facts.copper.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.copper.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.zinc.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_zinc)) },
                value = {
                    Text(
                        facts.zinc.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.zinc.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.sodium.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_sodium)) },
                value = {
                    Text(
                        facts.sodium.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.sodium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.iron.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null ||
                    facts.sodium.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_iron)) },
                value = {
                    Text(
                        facts.iron.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.iron.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.phosphorus.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null ||
                    facts.sodium.value != null ||
                    facts.iron.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_phosphorus)) },
                value = {
                    Text(
                        facts.phosphorus.map { it.inUnit(WeightUnit.Milligrams) }.stringResource(),
                        color = facts.phosphorus.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.selenium.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null ||
                    facts.sodium.value != null ||
                    facts.iron.value != null ||
                    facts.phosphorus.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_selenium)) },
                value = {
                    Text(
                        facts.selenium.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.selenium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.iodine.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null ||
                    facts.sodium.value != null ||
                    facts.iron.value != null ||
                    facts.phosphorus.value != null ||
                    facts.selenium.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_iodine)) },
                value = {
                    Text(
                        facts.iodine.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.iodine.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
        if (facts.chromium.value != null) {
            if (
                facts.manganese.value != null ||
                    facts.magnesium.value != null ||
                    facts.potassium.value != null ||
                    facts.calcium.value != null ||
                    facts.copper.value != null ||
                    facts.zinc.value != null ||
                    facts.sodium.value != null ||
                    facts.iron.value != null ||
                    facts.phosphorus.value != null ||
                    facts.selenium.value != null ||
                    facts.iodine.value != null
            ) {
                Spacer(Modifier.height(2.dp))
            }
            NutrientHolder(
                label = { Text(stringResource(Res.string.mineral_chromium)) },
                value = {
                    Text(
                        facts.chromium.map { it.inUnit(WeightUnit.Micrograms) }.stringResource(),
                        color = facts.chromium.color(),
                    )
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }
    }
}

@Composable
private fun Carbohydrates(
    facts: NutritionFacts,
    expanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val nutrientsPalette = LocalNutrientsPalette.current
    val corner =
        animateDpAsState(
            if (expanded) 16.dp else 4.dp,
            MaterialTheme.motionScheme.fastSpatialSpec(),
        )
    val color = nutrientsPalette.carbohydratesOnSurfaceContainer.copy(alpha = .1f)

    Column(
        modifier =
            modifier.graphicsLayer {
                clip = true
                shape = RoundedCornerShape(corner.value)
            }
    ) {
        val showAddedSugars = facts.addedSugars.value != null
        val showSugars = facts.sugars.value != null || showAddedSugars
        val showSolubleFiber = facts.solubleFiber.value != null
        val showInsolubleFiber = facts.insolubleFiber.value != null
        val showFiber = facts.dietaryFiber.value != null || showSolubleFiber || showInsolubleFiber
        val showCarbohydrates = facts.carbohydrates.value != null || showSugars || showFiber

        if (showCarbohydrates) {
            NutrientHolder(
                label = { Text(stringResource(Res.string.nutriment_carbohydrates)) },
                value = {
                    Text(facts.carbohydrates.stringResource(), color = facts.carbohydrates.color())
                },
                color = color,
                contentColor = MaterialTheme.colorScheme.onSurface,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Column {
                if (showSugars) {
                    Spacer(Modifier.height(2.dp))
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_sugars),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(facts.sugars.stringResource(), color = facts.sugars.color())
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showAddedSugars) {
                    if (showSugars || showCarbohydrates) Spacer(Modifier.height(2.dp))
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_added_sugars),
                                Modifier.padding(start = 16.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.addedSugars.stringResource(),
                                color = facts.addedSugars.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showFiber) {
                    if (showSugars || showAddedSugars || showCarbohydrates) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_fiber),
                                Modifier.padding(start = 8.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.dietaryFiber.stringResource(),
                                color = facts.dietaryFiber.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showSolubleFiber) {
                    if (showSugars || showAddedSugars || showFiber || showCarbohydrates) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_soluble_fiber),
                                Modifier.padding(start = 16.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.solubleFiber.stringResource(),
                                color = facts.solubleFiber.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
                if (showInsolubleFiber) {
                    if (
                        showSugars ||
                            showAddedSugars ||
                            showFiber ||
                            showSolubleFiber ||
                            showCarbohydrates
                    ) {
                        Spacer(Modifier.height(2.dp))
                    }
                    NutrientHolder(
                        label = {
                            Text(
                                stringResource(Res.string.nutriment_insoluble_fiber),
                                Modifier.padding(start = 16.dp),
                            )
                        },
                        value = {
                            Text(
                                facts.insolubleFiber.stringResource(),
                                color = facts.insolubleFiber.color(),
                            )
                        },
                        color = color,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
            }
        }
    }
}

@JvmName("NutrientValueEnergyStringResource")
@Composable
private fun NutrientValue<Energy>.stringResource(): String =
    value?.let {
        if (isIncomplete()) "* " + value?.stringResource() else value?.stringResource()
    } ?: stringResource(Res.string.not_available_short)

@JvmName("NutrientValueWeightStringResource")
@Composable
private fun NutrientValue<Weight>.stringResource(): String =
    value?.let {
        if (isIncomplete()) "* " + value?.stringResource() else value?.stringResource()
    } ?: stringResource(Res.string.not_available_short)

@Composable
private fun NutrientValue<*>.color(): Color =
    when (this) {
        is NutrientValue.Complete<*> -> MaterialTheme.colorScheme.onSurface
        is NutrientValue.Incomplete<*> -> MaterialTheme.colorScheme.onSurfaceVariant
    }

@Composable
private fun NutrientHolder(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = color,
        contentColor = contentColor,
        shape = shape,
    ) {
        Row(
            modifier =
                Modifier.fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                label()
                Spacer(Modifier.weight(1f).widthIn(min = 4.dp))
                value()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NutrientList() {
    PreviewFoodYouTheme {
        NutrientList(
            facts =
                NutritionFacts(
                    energy = NutrientValue.Complete(250.kilocalories),
                    proteins = NutrientValue.Complete(15.grams),
                    carbohydrates = NutrientValue.Complete(30.grams),
                    fats = NutrientValue.Complete(10.grams),
                    sugars = NutrientValue.Complete(5.grams),
                    saturatedFats = NutrientValue.Complete(2.grams),
                    solubleFiber = NutrientValue.Complete(3.grams),
                    salt = NutrientValue.Complete(0.5.grams),
                ),
            expanded = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
