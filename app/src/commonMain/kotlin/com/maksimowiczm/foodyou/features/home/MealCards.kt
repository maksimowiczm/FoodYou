package com.maksimowiczm.foodyou.features.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.NutritionCalculator
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.sum
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.LocalNutrientsPalette
import com.maksimowiczm.foodyou.shared.ui.component.FoodListItem
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealCards(
    meals: List<HomeMealState>,
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    onAdd: (MealIdentity) -> Unit,
    onEntry: (FoodDiaryEntryIdentity) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        meals.forEach {
            MealCard(
                state = it,
                shimmer = shimmer,
                onAdd = { onAdd(it.identity) },
                onEntry = onEntry,
            )
        }
    }
}

@Composable
private fun MealCard(
    state: HomeMealState,
    shimmer: Shimmer,
    onAdd: () -> Unit,
    onEntry: (FoodDiaryEntryIdentity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val sumNutrients =
        remember(state.foods) {
            state.foods.map { it.component.measuredNutritionFacts }.sum(NutritionFacts())
        }

    val header =
        @Composable {
            val interactionSource = remember { MutableInteractionSource() }
            val animatedShape =
                rememberInteractionAnimatedShape(
                    shapes =
                        InteractionShapes(
                            shape =
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 4.dp,
                                    bottomEnd = 4.dp,
                                ),
                            MaterialTheme.shapes.large,
                            MaterialTheme.shapes.large,
                            MaterialTheme.shapes.large,
                            MaterialTheme.shapes.large,
                        ),
                    interactionSource = interactionSource,
                )

            Box(
                Modifier.graphicsLayer {
                        clip = true
                        this.shape = animatedShape
                    }
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(),
                        enabled = state.foods.isNotEmpty(),
                        onClickLabel =
                            if (expanded) stringResource(Res.string.action_collapse)
                            else stringResource(Res.string.action_expand),
                        onClick = { expanded = !expanded },
                    )
            ) {
                Row(
                    modifier =
                        Modifier.heightIn(min = 64.dp)
                            .fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp,
                            ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = state.name,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(Modifier.weight(1f))
                    if (state.foods.isNotEmpty()) {
                        val rotation =
                            animateFloatAsState(
                                targetValue = if (expanded) 180f else 0f,
                                animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                            )
                        Box(
                            modifier = Modifier.size(48.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.KeyboardArrowUp,
                                contentDescription = null,
                                modifier =
                                    Modifier.size(24.dp).graphicsLayer {
                                        rotationZ = rotation.value
                                    },
                            )
                        }
                    }
                }
            }
        }

    val foods =
        @Composable {
            val nameSelector = LocalFoodNameSelector.current
            val dateFormatter = LocalDateFormatter.current

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                state.foods.forEach { food ->
                    key(food.identity.toString()) {
                        val interactionSource = remember { MutableInteractionSource() }
                        val shape =
                            rememberInteractionAnimatedShape(
                                shapes =
                                    InteractionShapes(
                                        shape =
                                            RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp,
                                                bottomStart = 4.dp,
                                                bottomEnd = 4.dp,
                                            ),
                                        pressedShape = MaterialTheme.shapes.large,
                                    ),
                                interactionSource = interactionSource,
                            )

                        Surface(
                            onClick = { onEntry(food.identity) },
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = shape,
                            interactionSource = interactionSource,
                        ) {
                            FoodListItem(
                                headline = { Text(nameSelector.select(food.component.name)) },
                                image =
                                    run {
                                        when (val image = food.component.image) {
                                            is FoodCompositionComponentImage.Blob -> {
                                                @Composable {
                                                    resolveBlob(image.blob)
                                                        .Image(shimmer, Modifier.size(56.dp))
                                                }
                                            }

                                            is FoodCompositionComponentImage.Uri -> {
                                                @Composable {
                                                    image.uri.Image(shimmer, Modifier.size(56.dp))
                                                }
                                            }

                                            null -> null
                                        }
                                    },
                                proteins = {
                                    Text(
                                        food.component.measuredNutritionFacts.proteins.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                carbohydrates = {
                                    Text(
                                        food.component.measuredNutritionFacts.carbohydrates.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                fats = {
                                    Text(
                                        food.component.measuredNutritionFacts.fats.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                energy = {
                                    Text(
                                        food.component.measuredNutritionFacts.energy.value
                                            ?.inUnit(LocalEnergyUnit.current)
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                quantity = {
                                    Text(food.component.quantity.stringResource())
                                },
                                overline = {
                                    Text(dateFormatter.formatTime(food.time))
                                },
                            )
                        }
                    }
                }
            }
        }

    val nutrients =
        @Composable
        nutrients@{
            val proteins = sumNutrients.proteins
            val carbohydrates = sumNutrients.carbohydrates
            val fats = sumNutrients.fats

            if (proteins.value == null || carbohydrates.value == null || fats.value == null)
                return@nutrients

            val nutrientsPalette = LocalNutrientsPalette.current
            val proteinsKcal =
                remember(proteins) {
                    NutritionCalculator.calculateProteinCalories(proteins.value!!)
                        .kilocalories
                        .toFloat()
                }
            val carbsKcal =
                remember(carbohydrates) {
                    NutritionCalculator.calculateCarbohydrateCalories(carbohydrates.value!!)
                        .kilocalories
                        .toFloat()
                }
            val fatsKcal =
                remember(fats) {
                    NutritionCalculator.calculateFatCalories(fats.value!!).kilocalories.toFloat()
                }
            val total =
                remember(proteinsKcal, carbsKcal, fatsKcal) {
                    (proteinsKcal + carbsKcal + fatsKcal)
                }

            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f).clip(MaterialTheme.shapes.large),
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (proteinsKcal > 0)
                        Box(
                            Modifier.height(4.dp)
                                .weight(proteinsKcal / total)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(nutrientsPalette.proteinsOnSurfaceContainer)
                        )
                    if (carbsKcal > 0)
                        Box(
                            Modifier.height(4.dp)
                                .weight(carbsKcal / total)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(nutrientsPalette.carbohydratesOnSurfaceContainer)
                        )
                    if (fatsKcal > 0)
                        Box(
                            Modifier.height(4.dp)
                                .weight(fatsKcal / total)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(nutrientsPalette.fatsOnSurfaceContainer)
                        )
                }
            }
        }

    val footer =
        @Composable {
            val energyUnit = LocalEnergyUnit.current
            val nutrientsPalette = LocalNutrientsPalette.current
            val nutrientsOrder = LocalNutrientsOrder.current

            Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                Row(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp,
                            ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = energyUnit.stringResource(),
                                style = MaterialTheme.typography.labelMedium,
                            )
                            Text(
                                text =
                                    sumNutrients.energy.value
                                        ?.toDouble(energyUnit)
                                        ?.roundToInt()
                                        ?.toString() ?: stringResource(Res.string.em_dash),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                        nutrientsOrder.forEach {
                            when (it) {
                                NutrientsOrder.Proteins ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(
                                            text =
                                                stringResource(Res.string.nutriment_proteins_short),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = nutrientsPalette.proteinsOnSurfaceContainer,
                                        )
                                        Text(
                                            text =
                                                sumNutrients.proteins.value?.stringResource()
                                                    ?: stringResource(Res.string.em_dash),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = nutrientsPalette.proteinsOnSurfaceContainer,
                                        )
                                    }

                                NutrientsOrder.Fats ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(
                                            text = stringResource(Res.string.nutriment_fats_short),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = nutrientsPalette.fatsOnSurfaceContainer,
                                        )
                                        Text(
                                            text =
                                                sumNutrients.fats.value?.stringResource()
                                                    ?: stringResource(Res.string.em_dash),
                                            style = MaterialTheme.typography.labelMedium,
                                            color = nutrientsPalette.fatsOnSurfaceContainer,
                                        )
                                    }

                                NutrientsOrder.Carbohydrates ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ) {
                                        Text(
                                            text =
                                                stringResource(
                                                    Res.string.nutriment_carbohydrates_short
                                                ),
                                            style = MaterialTheme.typography.labelMedium,
                                            color =
                                                nutrientsPalette.carbohydratesOnSurfaceContainer,
                                        )
                                        Text(
                                            text =
                                                sumNutrients.carbohydrates.value?.stringResource()
                                                    ?: stringResource(Res.string.em_dash),
                                            style = MaterialTheme.typography.labelMedium,
                                            color =
                                                nutrientsPalette.carbohydratesOnSurfaceContainer,
                                        )
                                    }

                                else -> Unit
                            }
                        }
                    }
                    FilledIconButton(
                        onClick = onAdd,
                        shapes =
                            IconButtonDefaults.shapes(
                                shape = MaterialTheme.shapes.medium,
                                pressedShape = CircleShape,
                            ),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Add,
                            contentDescription = stringResource(Res.string.action_add),
                        )
                    }
                }
            }
        }

    Column(modifier = modifier.clip(MaterialTheme.shapes.large)) {
        header()
        if (state.foods.isNotEmpty()) {
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(MaterialTheme.motionScheme.fastEffectsSpec()),
                exit = shrinkVertically(MaterialTheme.motionScheme.fastEffectsSpec()),
            ) {
                Column {
                    Spacer(Modifier.height(2.dp))
                    foods()
                }
            }
        }
        if (
            sumNutrients.proteins.value != null &&
                sumNutrients.carbohydrates.value != null &&
                sumNutrients.fats.value != null
        ) {
            Spacer(Modifier.height(2.dp))
            nutrients()
        }
        Spacer(Modifier.height(2.dp))
        footer()
    }
}

@Preview
@Composable
private fun MealCardsPreview() {
    PreviewFoodYouTheme {
        val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
        MealCards(
            meals = fakeHomeState.meals,
            contentPadding = PaddingValues(),
            shimmer = shimmer,
            onAdd = {},
            onEntry = {},
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}
