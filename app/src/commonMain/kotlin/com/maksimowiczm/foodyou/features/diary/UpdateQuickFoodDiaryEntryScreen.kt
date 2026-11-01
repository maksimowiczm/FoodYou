package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateBounds
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.maksimowiczm.foodyou.account.domain.NutrientsOrder
import com.maksimowiczm.foodyou.account.domain.Profile
import com.maksimowiczm.foodyou.fooddiary.domain.QuickFoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.shared.ui.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.shared.ui.component.Avatar
import com.maksimowiczm.foodyou.shared.ui.component.DiscardChangesDialog
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.form.rememberFormField
import com.maksimowiczm.foodyou.shared.ui.form.validateDouble
import com.maksimowiczm.foodyou.shared.ui.utility.EnergyFormatter.stringResource
import com.maksimowiczm.foodyou.shared.ui.utility.LocalEnergyUnit
import com.maksimowiczm.foodyou.shared.ui.utility.LocalNutrientsOrder
import com.maksimowiczm.foodyou.shared.ui.utility.LocalUIFeatureFlags
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import foodyou.app.generated.resources.*
import io.konform.validation.ifPresent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
context(animatedContentScope: AnimatedContentScope)
fun UpdateQuickFoodDiaryEntryScreen(
    onBack: () -> Unit,
    onUpdate: () -> Unit,
    entryId: QuickFoodDiaryEntryId,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateQuickFoodDiaryEntryViewModel = koinViewModel {
        parametersOf(entryId)
    }

    LaunchedCollectWithLifecycle(viewModel.updatedEvent) { onUpdate() }

    when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
        UpdateQuickFoodDiaryEntryUiState.Loading -> LoadingScreen(onBack, modifier)
        is UpdateQuickFoodDiaryEntryUiState.Ready -> {
            val selectedProfiles =
                remember(uiState.profiles, uiState.selectedProfileIds) {
                    uiState.profiles.filter { it.id in uiState.selectedProfileIds }
                }

            UpdateQuickFoodDiaryEntryScreenContent(
                initialName = uiState.entry.name,
                initialProteins = uiState.entry.nutritionFacts.proteins.value?.grams,
                initialCarbohydrates = uiState.entry.nutritionFacts.carbohydrates.value?.grams,
                initialFats = uiState.entry.nutritionFacts.fats.value?.grams,
                initialEnergy =
                    uiState.entry.nutritionFacts.energy.value
                        ?.inUnit(uiState.energyUnit)
                        ?.kilocalories,
                profiles = uiState.profiles,
                selectedProfiles = selectedProfiles,
                meals = uiState.meals,
                selectedMeal = uiState.selectedMeal,
                selectedDateTime = uiState.selectedDateTime,
                onBack = onBack,
                onSave = { name, proteins, carbohydrates, fats, energy ->
                    viewModel.update(
                        name = name,
                        proteins = proteins,
                        carbohydrates = carbohydrates,
                        fats = fats,
                        energyValue = energy,
                    )
                },
                onSelectProfiles = {
                    viewModel.selectProfiles(it.map { profile -> profile.id })
                },
                onSelectMeal = { viewModel.selectMeal(it.id) },
                onSelectDate = viewModel::selectDate,
                onSelectTime = viewModel::selectTime,
                modifier = modifier,
            )
        }
    }
}

@Composable
context(animatedContentScope: AnimatedContentScope)
private fun UpdateQuickFoodDiaryEntryScreenContent(
    initialName: String,
    initialProteins: Double?,
    initialCarbohydrates: Double?,
    initialFats: Double?,
    initialEnergy: Double?,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    meals: List<Meal>,
    selectedMeal: Meal,
    selectedDateTime: LocalDateTime,
    onBack: () -> Unit,
    onSave:
        (
            name: String,
            proteins: Double?,
            carbohydrates: Double?,
            fats: Double?,
            energy: Double?,
        ) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    onSelectMeal: (Meal) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectTime: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val requiredStr = stringResource(Res.string.neutral_required)

    val nameFormField =
        rememberFormField(defaultValue = initialName) {
            constrain(requiredStr) { !it.isNullOrBlank() }
        }
    val proteinsFormField = rememberDoubleFormField(initialValue = initialProteins)
    val carbohydratesFormField = rememberDoubleFormField(initialValue = initialCarbohydrates)
    val fatsFormField = rememberDoubleFormField(initialValue = initialFats)
    val energyFormField = rememberDoubleFormField(initialValue = initialEnergy)

    val isModified by remember {
        derivedStateOf {
            nameFormField.isModified ||
                proteinsFormField.isModified ||
                carbohydratesFormField.isModified ||
                fatsFormField.isModified ||
                energyFormField.isModified
        }
    }
    val isValid by remember {
        derivedStateOf {
            nameFormField.textFieldState.text.isNotBlank() &&
                nameFormField.isValid &&
                proteinsFormField.isValid &&
                carbohydratesFormField.isValid &&
                fatsFormField.isValid &&
                energyFormField.isValid
        }
    }

    val nutrientsOrder = LocalNutrientsOrder.current
    val energyUnit = LocalEnergyUnit.current
    val motionScheme = MaterialTheme.motionScheme
    val featureFlags = LocalUIFeatureFlags.current
    val showProfiles = !featureFlags.singleProfileMode && profiles.size > 1

    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    if (showDiscardDialog) {
        DiscardChangesDialog(
            onDismissRequest = { showDiscardDialog = false },
            onDiscard = onBack,
        )
    }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = isModified,
        onBackCompleted = { showDiscardDialog = true },
    )

    val nameFocusRequester = remember { FocusRequester() }
    var profilesExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.semantics(mergeDescendants = true) {},
                title = {
                    Text(
                        text = stringResource(Res.string.headline_quick_add),
                        modifier = Modifier.semantics { heading() },
                    )
                },
                subtitle = { Text(stringResource(Res.string.description_quick_add)) },
                navigationIcon = {
                    ArrowBackIconButton(
                        onClick = {
                            if (isModified) showDiscardDialog = true else onBack()
                        }
                    )
                },
                scrollBehavior = scrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    ),
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = {
                    if (isValid) {
                        onSave(
                            nameFormField.textFieldState.text.toString().trim(),
                            proteinsFormField.textFieldState.text.toString().toDoubleOrNull(),
                            carbohydratesFormField.textFieldState.text.toString().toDoubleOrNull(),
                            fatsFormField.textFieldState.text.toString().toDoubleOrNull(),
                            energyFormField.textFieldState.text.toString().toDoubleOrNull(),
                        )
                    }
                },
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible = !animatedContentScope.transition.isRunning && isValid,
                        alignment = Alignment.BottomCenter,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(Res.string.action_save))
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier.imePadding()
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = paddingValues.add(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item {
                DiaryDateTimePicker(
                    meals = meals,
                    selectedMeal = selectedMeal,
                    selectedDateTime = selectedDateTime,
                    onMealChange = onSelectMeal,
                    onDateChange = onSelectDate,
                    onTimeChange = onSelectTime,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                if (showProfiles) {
                    AnimatedVisibility(
                        visible = profilesExpanded,
                        enter =
                            fadeIn(motionScheme.fastEffectsSpec()) +
                                expandVertically(motionScheme.defaultSpatialSpec()),
                        exit =
                            fadeOut(motionScheme.fastEffectsSpec()) +
                                shrinkVertically(motionScheme.fastEffectsSpec()),
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            profiles.forEachIndexed { index, profile ->
                                SegmentedListItem(
                                    selected = profile in selectedProfiles,
                                    onClick = {
                                        val selected = selectedProfiles.toMutableList()
                                        if (profile in selected && selected.size > 1)
                                            selected.remove(profile)
                                        else selected.add(profile)
                                        onSelectProfiles(selected.distinct())
                                    },
                                    shapes =
                                        ListItemDefaults.segmentedShapes(
                                            index = index,
                                            count = profiles.size,
                                        ),
                                    colors =
                                        ListItemDefaults.segmentedColors(
                                            containerColor =
                                                MaterialTheme.colorScheme.surfaceContainer
                                        ),
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        Box(
                                            modifier =
                                                Modifier.size(40.dp)
                                                    .clip(CircleShape)
                                                    .wrapContentSize(unbounded = true),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            profile.avatar.Avatar(
                                                when (profile.avatar) {
                                                    is Profile.Avatar.Photo -> Modifier.size(40.dp)
                                                    is Profile.Avatar.Predefined ->
                                                        Modifier.padding(8.dp)
                                                            .size(24.dp)
                                                            .wrapContentSize(unbounded = true)
                                                }
                                            )
                                        }
                                        Text(profile.name)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val error = nameFormField.error
                    val nameLabelStr = stringResource(Res.string.product_name)
                    val labelText = remember(nameLabelStr) { "$nameLabelStr *" }
                    OutlinedTextField(
                        state = nameFormField.textFieldState,
                        label = { Text(labelText) },
                        modifier = Modifier.weight(1f).focusRequester(nameFocusRequester),
                        isError = error != null,
                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                            ),
                    )
                    if (showProfiles) {
                        LookaheadScope {
                            QuickProfileStack(
                                selectedProfiles = selectedProfiles,
                                expanded = profilesExpanded,
                                onExpandedChange = { profilesExpanded = it },
                            )
                        }
                    }
                }
            }
            nutrientsOrder.forEach { nutrient ->
                when (nutrient) {
                    NutrientsOrder.Proteins -> {
                        item {
                            val error = proteinsFormField.error
                            OutlinedTextField(
                                state = proteinsFormField.textFieldState,
                                label = { Text(stringResource(Res.string.nutriment_proteins)) },
                                modifier = Modifier.fillMaxWidth(),
                                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                                isError = error != null,
                                supportingText = error?.let { { Text(it) } },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next,
                                    ),
                            )
                        }
                    }

                    NutrientsOrder.Carbohydrates -> {
                        item {
                            val error = carbohydratesFormField.error
                            OutlinedTextField(
                                state = carbohydratesFormField.textFieldState,
                                label = {
                                    Text(stringResource(Res.string.nutriment_carbohydrates))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                                isError = error != null,
                                supportingText = error?.let { { Text(it) } },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next,
                                    ),
                            )
                        }
                    }

                    NutrientsOrder.Fats -> {
                        item {
                            val error = fatsFormField.error
                            OutlinedTextField(
                                state = fatsFormField.textFieldState,
                                label = { Text(stringResource(Res.string.nutriment_fats)) },
                                modifier = Modifier.fillMaxWidth(),
                                suffix = { Text(stringResource(Res.string.unit_gram_short)) },
                                isError = error != null,
                                supportingText = error?.let { { Text(it) } },
                                keyboardOptions =
                                    KeyboardOptions(
                                        keyboardType = KeyboardType.Decimal,
                                        imeAction = ImeAction.Next,
                                    ),
                            )
                        }
                    }

                    NutrientsOrder.Other,
                    NutrientsOrder.Vitamins,
                    NutrientsOrder.Minerals -> Unit
                }
            }
            item {
                val error = energyFormField.error
                OutlinedTextField(
                    state = energyFormField.textFieldState,
                    label = { Text(stringResource(Res.string.unit_energy)) },
                    modifier = Modifier.fillMaxWidth(),
                    suffix = { Text(energyUnit.stringResource()) },
                    isError = error != null,
                    supportingText = error?.let { { Text(it) } },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done,
                        ),
                )
            }
        }
    }
}

@Composable
private fun LookaheadScope.QuickProfileStack(
    selectedProfiles: List<ProfileUiState>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motionScheme = MaterialTheme.motionScheme
    Row(
        modifier =
            modifier.toggleable(
                value = expanded,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onValueChange = { onExpandedChange(!expanded) },
            ),
        horizontalArrangement = Arrangement.spacedBy((-25).dp),
    ) {
        selectedProfiles.forEachIndexed { index, profile ->
            key(profile.id) {
                Box(
                    modifier =
                        Modifier.animateBounds(
                                lookaheadScope = this@QuickProfileStack,
                                boundsTransform = { _, _ -> motionScheme.fastSpatialSpec() },
                                animateMotionFrameOfReference = true,
                            )
                            .zIndex(selectedProfiles.size - index + 1f)
                            .size(40.dp)
                            .clip(CircleShape)
                            .wrapContentSize(unbounded = true)
                            .shadow(4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    val avatarModifier =
                        when (profile.avatar) {
                            is Profile.Avatar.Photo -> Modifier.size(40.dp)
                            is Profile.Avatar.Predefined ->
                                Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(8.dp)
                                    .size(24.dp)
                                    .wrapContentSize(unbounded = true)
                        }
                    profile.avatar.Avatar(avatarModifier)
                }
            }
        }
    }
}

@Composable
private fun rememberDoubleFormField(initialValue: Double? = null): FormField {
    val invalidNumber = stringResource(Res.string.error_invalid_number)
    val valueCannotBeNegative = stringResource(Res.string.error_value_cannot_be_negative)

    return rememberFormField(defaultValue = initialValue?.formatCompact()) {
        ifPresent {
            validateDouble {
                constrain(invalidNumber) { it != null }
                constrain(valueCannotBeNegative) { it?.let { it >= 0 } ?: true }
            }
        }
    }
}
