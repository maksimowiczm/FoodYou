package com.maksimowiczm.foodyou.features.personalization

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.theme.PreviewFoodYouTheme
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.extension.confirm
import com.maksimowiczm.foodyou.shared.ui.extension.toggle
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PersonalizationScreen(
    onBack: () -> Unit,
    onNutritionFacts: () -> Unit,
    onMeals: () -> Unit,
    onColors: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: PersonalizationViewModel = koinViewModel()

    val singleProfileMode by viewModel.singleProfileMode.collectAsStateWithLifecycle()
    val singleProfileModeAllowed by viewModel.singleProfileModeAllowed.collectAsStateWithLifecycle()
    val energyUnit by viewModel.energyUnit.collectAsStateWithLifecycle()
    val hideScreen by viewModel.hideScreen.collectAsStateWithLifecycle()

    PersonalizationScreen(
        singleProfileMode = singleProfileMode,
        allowSingleProfileMode = singleProfileModeAllowed,
        secureScreen = hideScreen,
        energyUnit = energyUnit,
        onBack = onBack,
        onNutritionFacts = onNutritionFacts,
        onMeals = onMeals,
        onColors = onColors,
        onUpdateEnergyUnit = viewModel::updateEnergyUnit,
        onUpdateSecureScreen = viewModel::updateSecureScreen,
        onUpdateSingleProfileMode = viewModel::updateSingleProfileMode,
        modifier = modifier,
    )
}

@Composable
fun PersonalizationScreen(
    singleProfileMode: Boolean,
    allowSingleProfileMode: Boolean,
    secureScreen: Boolean,
    energyUnit: EnergyUnit,
    onBack: () -> Unit,
    onNutritionFacts: () -> Unit,
    onMeals: () -> Unit,
    onColors: () -> Unit,
    onUpdateEnergyUnit: (EnergyUnit) -> Unit,
    onUpdateSingleProfileMode: (Boolean) -> Unit,
    onUpdateSecureScreen: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var energyExpanded by rememberSaveable { mutableStateOf(false) }
    val expandedEdges =
        animateDpAsState(
            targetValue = if (energyExpanded) 16.dp else 4.dp,
            animationSpec = MaterialTheme.motionScheme.fastEffectsSpec(),
        )

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                title = { Text(stringResource(Res.string.headline_personalization)) },
                navigationIcon = { ArrowBackIconButton(onBack) },
                subtitle = { Text(stringResource(Res.string.description_personalization)) },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        val colors =
            ListItemDefaults.segmentedColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )

        LazyColumn(
            modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(8.dp),
        ) {
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier =
                        Modifier.graphicsLayer {
                            val edges = expandedEdges.value
                            shape =
                                RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = edges,
                                    bottomEnd = edges,
                                )
                            clip = true
                        },
                ) {
                    SegmentedListItem(
                        onClick = onNutritionFacts,
                        shapes = ListItemDefaults.shapes(),
                        colors = colors,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.List,
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            Text(
                                stringResource(
                                    Res.string.description_personalize_nutrition_facts_short
                                )
                            )
                        },
                        content = { Text(stringResource(Res.string.headline_nutrition_facts)) },
                    )
                    SegmentedListItem(
                        onClick = onMeals,
                        shapes = ListItemDefaults.shapes(),
                        colors = colors,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Restaurant,
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            Text(stringResource(Res.string.neutral_set_your_meal_schedule))
                        },
                        content = { Text(stringResource(Res.string.headline_meals)) },
                    )
                }
            }
            item {
                val spacerHeight by
                    animateDpAsState(
                        targetValue = if (energyExpanded) 8.dp else 2.dp,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    )
                Spacer(Modifier.height(spacerHeight))
                EnergyUnitSection(
                    expanded = energyExpanded,
                    onExpandedChange = { energyExpanded = it },
                    energyUnit = energyUnit,
                    onUpdateEnergyUnit = onUpdateEnergyUnit,
                    colors = colors,
                    modifier =
                        Modifier.graphicsLayer {
                            val edges = expandedEdges.value
                            shape = RoundedCornerShape(edges)
                            clip = true
                        },
                )
                Spacer(Modifier.height(spacerHeight))
            }
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier =
                        Modifier.graphicsLayer {
                            val edges = expandedEdges.value
                            shape =
                                RoundedCornerShape(
                                    topStart = edges,
                                    topEnd = edges,
                                    bottomEnd = 16.dp,
                                    bottomStart = 16.dp,
                                )
                            clip = true
                        },
                ) {
                    SegmentedListItem(
                        onClick = onColors,
                        shapes = ListItemDefaults.shapes(),
                        colors = colors,
                        leadingContent = {
                            Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
                        },
                        supportingContent = { Text(stringResource(Res.string.description_colors)) },
                        content = { Text(stringResource(Res.string.headline_colors)) },
                    )
                    SegmentedListItem(
                        checked = singleProfileMode,
                        onCheckedChange = {
                            onUpdateSingleProfileMode(it)
                            hapticFeedback.toggle(it)
                        },
                        shapes = ListItemDefaults.shapes(),
                        enabled = allowSingleProfileMode,
                        colors = colors,
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Groups,
                                contentDescription = null,
                            )
                        },
                        supportingContent = {
                            if (!allowSingleProfileMode)
                                Text(stringResource(Res.string.error_single_profile_mode))
                            else Text(stringResource(Res.string.description_single_profile_mode))
                        },
                        trailingContent = {
                            Switch(
                                checked = singleProfileMode,
                                onCheckedChange = null,
                                enabled = allowSingleProfileMode,
                            )
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            Text(stringResource(Res.string.headline_single_profile_mode))
                        },
                    )
                    SegmentedListItem(
                        checked = secureScreen,
                        onCheckedChange = {
                            onUpdateSecureScreen(!secureScreen)
                            hapticFeedback.toggle(!secureScreen)
                        },
                        shapes = ListItemDefaults.shapes(),
                        colors = colors,
                        leadingContent = {
                            Icon(imageVector = Icons.Outlined.Lock, contentDescription = null)
                        },
                        supportingContent = {
                            Text(stringResource(Res.string.action_prevent_screen_capture))
                        },
                        trailingContent = {
                            Switch(checked = secureScreen, onCheckedChange = null)
                        },
                        verticalAlignment = Alignment.CenterVertically,
                        content = { Text(stringResource(Res.string.headline_secure_screen)) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EnergyUnitSection(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    energyUnit: EnergyUnit,
    onUpdateEnergyUnit: (EnergyUnit) -> Unit,
    colors: ListItemColors,
    modifier: Modifier = Modifier,
) {
    val hapticFeedback = LocalHapticFeedback.current
    val transition = updateTransition(expanded, label = "EnergyExpanded")

    Column(modifier) {
        SegmentedListItem(
            onClick = { onExpandedChange(!expanded) },
            shapes = ListItemDefaults.shapes(),
            colors = colors,
            leadingContent = { Icon(Icons.Outlined.Bolt, contentDescription = null) },
            supportingContent = {
                transition.Crossfade { isExpanded ->
                    if (isExpanded) Text(stringResource(Res.string.description_energy_unit))
                    else
                        Text(
                            text = energyUnit.stringResource(),
                            color = MaterialTheme.colorScheme.primary,
                        )
                }
            },
            trailingContent = {
                val containerColor by
                    animateColorAsState(
                        if (expanded) MaterialTheme.colorScheme.surfaceContainer
                        else MaterialTheme.colorScheme.surface,
                        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                    )
                val rotationState =
                    animateFloatAsState(
                        if (expanded) 180f else 0f,
                        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                    )
                Surface(
                    color = containerColor,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = CircleShape,
                ) {
                    Box(
                        modifier = Modifier.width(32.dp).height(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowUp,
                            contentDescription = null,
                            modifier = Modifier.graphicsLayer { rotationZ = rotationState.value },
                        )
                    }
                }
            },
            content = { Text(stringResource(Res.string.headline_energy_unit)) },
        )
        transition.AnimatedVisibility(
            visible = { it },
            enter =
                fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    expandIn(MaterialTheme.motionScheme.defaultSpatialSpec()),
            exit =
                fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                    shrinkOut(MaterialTheme.motionScheme.fastSpatialSpec()),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                EnergyUnit.entries.forEach { unit ->
                    SegmentedListItem(
                        onClick = {
                            onUpdateEnergyUnit(unit)
                            hapticFeedback.confirm()
                        },
                        selected = energyUnit == unit,
                        shapes = ListItemDefaults.shapes(),
                        colors = colors,
                        content = { Text(unit.stringResource()) },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PersonalizationScreenPreview() {
    PreviewFoodYouTheme {
        PersonalizationScreen(
            singleProfileMode = false,
            allowSingleProfileMode = false,
            secureScreen = false,
            energyUnit = EnergyUnit.Kilocalories,
            onBack = {},
            onNutritionFacts = {},
            onMeals = {},
            onColors = {},
            onUpdateEnergyUnit = {},
            onUpdateSingleProfileMode = {},
            onUpdateSecureScreen = {},
        )
    }
}
