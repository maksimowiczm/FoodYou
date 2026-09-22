package com.maksimowiczm.foodyou.features.home.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.capabilities.theme.LocalNutrientsPalette
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.NutritionCalculator
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.sum
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.component.FoodListItem
import com.maksimowiczm.foodyou.shared.ui.component.Image
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.saveable.jsonSaver
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalDateFormatter
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.LocalUIFeatureFlags
import com.maksimowiczm.foodyou.shared.ui.utility.QuantityFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.WeightFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import foodyou.app.generated.resources.*
import kotlin.math.roundToInt
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MealCards(
    meals: List<HomeMealState>,
    shimmer: Shimmer,
    contentPadding: PaddingValues,
    onAdd: (MealId) -> Unit,
    onEntry: (FoodDiaryEntryId) -> Unit,
    onDeleteEntry: (FoodDiaryEntryId) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(contentPadding),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        meals.forEach {
            when (it) {
                is HomeMealState.Linked ->
                    MealCard(
                        state = it,
                        shimmer = shimmer,
                        onEntry = onEntry,
                        onAdd = { onAdd(it.id) },
                        onDeleteEntry = onDeleteEntry,
                    )
                is HomeMealState.Unlinked ->
                    MealCard(
                        state = it,
                        shimmer = shimmer,
                        onEntry = onEntry,
                        onDeleteEntry = onDeleteEntry,
                    )
            }
        }
    }
}

@Composable
private fun MealCard(
    state: HomeMealState,
    shimmer: Shimmer,
    onEntry: (FoodDiaryEntryId) -> Unit,
    onDeleteEntry: (FoodDiaryEntryId) -> Unit,
    modifier: Modifier = Modifier,
    onAdd: (() -> Unit)? = null,
) {
    var expanded by rememberSaveable { mutableStateOf(true) }
    val sumNutrients =
        remember(state.foods) {
            state.foods.map { it.snapshot.measuredNutritionFacts }.sum(NutritionFacts())
        }

    var selectedEntryId by
        rememberSaveable(stateSaver = jsonSaver()) { mutableStateOf<FoodDiaryEntryId?>(null) }
    val selectedFood =
        remember(selectedEntryId, state.foods) {
            state.foods.singleOrNull { it.id == selectedEntryId }
        }
    if (selectedFood != null)
        AlertDialog(
            onDismissRequest = { selectedEntryId = null },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteEntry(selectedFood.id)
                        selectedEntryId = null
                    },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.buttonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_delete))
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                )
            },
            title = { Text(stringResource(Res.string.question_delete_diary_entry)) },
            text = { Text(stringResource(Res.string.description_delete_diary_entry)) },
            dismissButton = {
                TextButton(
                    onClick = { selectedEntryId = null },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(Res.string.action_cancel))
                }
            },
        )

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
                        text =
                            when (state) {
                                is HomeMealState.Linked -> state.name
                                is HomeMealState.Unlinked ->
                                    stringResource(Res.string.headline_meal_unlinked)
                            },
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
            val featureFlags = LocalUIFeatureFlags.current
            val motionScheme = MaterialTheme.motionScheme
            val transition = updateTransition(state.foods)

            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.fillMaxWidth(),
                enter = expandVertically(motionScheme.fastEffectsSpec()),
                exit = shrinkVertically(motionScheme.fastEffectsSpec()),
            ) {
                transition.AnimatedContent(
                    modifier = Modifier.fillMaxWidth(),
                    transitionSpec = {
                        fadeIn(motionScheme.fastEffectsSpec()) togetherWith
                            fadeOut(motionScheme.fastEffectsSpec())
                    },
                    contentKey = { it.size },
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Spacer(Modifier)
                        it.forEach { food ->
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
                            val progress = rememberPressProgress(interactionSource)

                            FoodListItem(
                                headline = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(nameSelector.select(food.snapshot.name))
                                        if (food.snapshot.snapshot is CompositeFoodSnapshot) {
                                            Spacer(Modifier.width(8.dp))
                                            Icon(
                                                painter =
                                                    painterResource(Res.drawable.ic_skillet_filled),
                                                contentDescription = null,
                                            )
                                        }
                                    }
                                },
                                image =
                                    run {
                                        when (val image = food.snapshot.image) {
                                            is FoodSnapshotImage.Blob -> {
                                                @Composable {
                                                    resolveBlob(image.blob)
                                                        .Image(
                                                            shimmer,
                                                            Modifier.size(56.dp),
                                                        )
                                                }
                                            }

                                            is FoodSnapshotImage.Uri -> {
                                                @Composable {
                                                    image.uri.Image(
                                                        shimmer,
                                                        Modifier.size(56.dp),
                                                    )
                                                }
                                            }

                                            null -> null
                                        }
                                    },
                                proteins = {
                                    Text(
                                        food.snapshot.measuredNutritionFacts.proteins.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                carbohydrates = {
                                    Text(
                                        food.snapshot.measuredNutritionFacts.carbohydrates.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                fats = {
                                    Text(
                                        food.snapshot.measuredNutritionFacts.fats.value
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                energy = {
                                    Text(
                                        food.snapshot.measuredNutritionFacts.energy.value
                                            ?.inUnit(LocalEnergyUnit.current)
                                            ?.stringResource() ?: "?"
                                    )
                                },
                                quantity = {
                                    Text(food.snapshot.quantity.stringResource())
                                },
                                modifier =
                                    Modifier.graphicsLayer {
                                            this.shape = shape
                                            clip = true
                                            alpha =
                                                if (selectedEntryId == food.id) .25f
                                                else lerp(1f, .25f, progress.value)
                                        }
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .combinedClickable(
                                            interactionSource = interactionSource,
                                            indication = ripple(),
                                            onClick = { onEntry(food.id) },
                                            onLongClick = { selectedEntryId = food.id },
                                        ),
                                overline =
                                    if (featureFlags.foodDiaryEntryTimestamps) {
                                        @Composable {
                                            val dateFormatter = LocalDateFormatter.current
                                            Text(dateFormatter.formatTime(food.time))
                                        }
                                    } else null,
                            )
                        }
                    }
                }
            }
        }

    val nutrients =
        @Composable
        nutrients@{
            val proteins = sumNutrients.proteins.value
            val carbohydrates = sumNutrients.carbohydrates.value
            val fats = sumNutrients.fats.value

            if (proteins == null || carbohydrates == null || fats == null) {
                val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

                return@nutrients Canvas(
                    Modifier.fillMaxWidth().height(4.dp).clip(MaterialTheme.shapes.extraSmall)
                ) {
                    drawRect(
                        color = surfaceVariant,
                        topLeft = Offset(0f, 0f),
                        size = Size(size.width, size.height),
                    )
                }
            }

            val nutrientsPalette = LocalNutrientsPalette.current
            val proteinsKcal =
                animateFloatAsState(
                    remember(proteins) {
                        NutritionCalculator.calculateProteinCalories(proteins)
                            .kilocalories
                            .toFloat()
                    },
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )
            val carbsKcal =
                animateFloatAsState(
                    remember(carbohydrates) {
                        NutritionCalculator.calculateCarbohydrateCalories(carbohydrates)
                            .kilocalories
                            .toFloat()
                    },
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )
            val fatsKcal =
                animateFloatAsState(
                    remember(fats) {
                        NutritionCalculator.calculateFatCalories(fats).kilocalories.toFloat()
                    },
                    animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
                )

            val proteinsColor = nutrientsPalette.proteinsOnSurfaceContainer
            val carbsColor = nutrientsPalette.carbohydratesOnSurfaceContainer
            val fatsColor = nutrientsPalette.fatsOnSurfaceContainer

            Canvas(Modifier.fillMaxWidth().height(4.dp)) {
                val total = proteinsKcal.value + carbsKcal.value + fatsKcal.value
                if (total <= 0f) return@Canvas

                val gapPx = 2.dp.toPx()
                val visibleCount =
                    listOf(proteinsKcal.value, carbsKcal.value, fatsKcal.value).count { it > 0f }
                val availableWidth = size.width - gapPx * (visibleCount - 1).coerceAtLeast(0)
                val cornerRadius = CornerRadius(size.height / 2f)

                var startX = 0f
                fun drawSegment(value: Float, color: Color) {
                    if (value <= 0f) return
                    val width = availableWidth * (value / total)
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(startX, 0f),
                        size = Size(width, size.height),
                        cornerRadius = cornerRadius,
                    )
                    startX += width + gapPx
                }

                drawSegment(proteinsKcal.value, proteinsColor)
                drawSegment(carbsKcal.value, carbsColor)
                drawSegment(fatsKcal.value, fatsColor)
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
                    if (onAdd != null)
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

    Column(modifier.clip(MaterialTheme.shapes.large)) {
        header()
        foods()
        Spacer(Modifier.height(2.dp))
        nutrients()
        Spacer(Modifier.height(2.dp))
        footer()
    }
}

@Composable
private fun rememberPressProgress(
    interactionSource: MutableInteractionSource,
    durationMillis: Long = LocalViewConfiguration.current.longPressTimeoutMillis,
    releaseAnimationSpec: AnimationSpec<Float> = MaterialTheme.motionScheme.fastEffectsSpec(),
): State<Float> {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    var startNanos = -1L
                    while (currentCoroutineContext().isActive) {
                        val value = withFrameNanos { frameNanos ->
                            if (startNanos < 0L) startNanos = frameNanos
                            val elapsedMillis = (frameNanos - startNanos) / 1_000_000f
                            (elapsedMillis / durationMillis).coerceIn(0f, 1f)
                        }
                        progress.snapTo(value)
                        if (value >= 1f) break
                    }
                }
                is PressInteraction.Release,
                is PressInteraction.Cancel ->
                    progress.animateTo(
                        targetValue = 0f,
                        animationSpec = releaseAnimationSpec,
                    )
            }
        }
    }

    return progress.asState()
}

@Preview
@Composable
private fun MealCardsPreview() {
    PreviewFoodYouTheme {
        MealCards(
            meals = HomeUiStateProvider().uiState.meals,
            contentPadding = PaddingValues(),
            shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View),
            onAdd = {},
            onEntry = {},
            onDeleteEntry = {},
            modifier = Modifier.verticalScroll(rememberScrollState()),
        )
    }
}
