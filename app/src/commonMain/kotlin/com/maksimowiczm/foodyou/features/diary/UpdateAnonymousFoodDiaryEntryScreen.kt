package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodIngredients
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientsExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotImage
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.common.domain.food.toAbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import kotlin.time.Instant
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
context(animatedContentScope: AnimatedContentScope)
fun UpdateAnonymousFoodDiaryEntryScreen(
    onBack: () -> Unit,
    onRelink: (List<ProfileId>, Instant, MeasuredFoodSnapshot) -> Unit,
    onSave: (Quantity, List<ProfileId>, Instant) -> Unit,
    entry: FoodDiaryEntry,
    profiles: List<ProfileUiState>,
    modifier: Modifier = Modifier,
) {
    val viewModel: UpdateAnonymousFoodDiaryEntryViewModel = koinViewModel {
        parametersOf(entry.snapshot)
    }

    when (val uiState = viewModel.uiState.collectAsStateWithLifecycle().value) {
        UpdateAnonymousFoodDiaryEntryUiState.Loading -> LoadingScreen(onBack, modifier)
        is UpdateAnonymousFoodDiaryEntryUiState.Loaded -> {
            val defaultValue =
                remember(entry.snapshot.quantity) {
                    entry.snapshot.quantity.toQuantity().amount.formatCompact()
                }
            val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)
            var selectedProfileIds by rememberSerializable {
                mutableStateOf(entry.profileIds.toList())
            }
            val selectedProfiles =
                remember(profiles, selectedProfileIds) {
                    profiles.filter { it.id in selectedProfileIds }
                }

            LaunchedEffect(formField.textFieldState.text, uiState.selectedQuantityType) {
                viewModel.selectQuantity(
                    formField.textFieldState.text.toString().toDoubleOrNull(),
                    uiState.selectedQuantityType,
                )
            }

            UpdateAnonymousFoodDiaryEntryScreenContent(
                headline = LocalFoodNameSelector.current.select(uiState.snapshot.name),
                image =
                    uiState.snapshot.image?.let {
                        when (it) {
                            is FoodSnapshotImage.Uri -> it.uri
                            is FoodSnapshotImage.Blob -> resolveBlob(it.blob)
                        }
                    },
                suggestions = uiState.suggestions,
                scaledNutritionFacts = uiState.scaledNutritionFacts,
                ingredientScalingFactor = uiState.ingredientScalingFactor,
                packageQuantity = entry.snapshot.quantity.packageWeight?.toAbsoluteQuantity(),
                servingQuantity = entry.snapshot.quantity.servingWeight?.toAbsoluteQuantity(),
                components =
                    (uiState.snapshot as? CompositeFoodSnapshot)?.components ?: emptyList(),
                types = uiState.quantityTypes,
                selectedType = uiState.selectedQuantityType,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                isTracked = uiState.isTracked,
                onIsTrackedChange = viewModel::setIsTracked,
                onBack = onBack,
                onSave = { trackFood ->
                    if (trackFood)
                        onRelink(selectedProfileIds, entry.timestamp, uiState.relinkedSnapshot)
                    else onSave(uiState.selectedQuantity, selectedProfileIds, entry.timestamp)
                },
                onSelectQuantity = viewModel::selectQuantity,
                onSelectQuantityType = viewModel::selectQuantityType,
                onSelectProfiles = { selectedProfiles ->
                    selectedProfileIds = selectedProfiles.map { profile -> profile.id }
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
context(animatedContentScope: AnimatedContentScope)
private fun UpdateAnonymousFoodDiaryEntryScreenContent(
    headline: String?,
    image: FileUri?,
    suggestions: List<Quantity>,
    scaledNutritionFacts: NutritionFacts?,
    ingredientScalingFactor: Double,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    components: List<MeasuredFoodSnapshot>,
    types: List<QuantityType>,
    selectedType: QuantityType,
    formField: FormField,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    isTracked: Boolean,
    onIsTrackedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onSave: (trackFood: Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientsExpanded()
    val expandingEnabled =
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    val focusRequester = remember { FocusRequester() }
    var focusRequested by rememberSaveable { mutableStateOf(value = false) }
    LaunchedEffect(Unit) {
        if (!focusRequested) {
            val _ = runCatching { focusRequester.requestFocus() }
            focusRequested = true
        }
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            FoodScreenTopBar(
                onBack = onBack,
                title = headline,
                actions = {},
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            AddFoodDiaryEntryFab(
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            !animatedContentScope.transition.isRunning &&
                                formField.error == null &&
                                selectedProfiles.isNotEmpty(),
                        alignment = Alignment.BottomCenter,
                    ),
                isTracked = isTracked,
                onIsTrackedChange = onIsTrackedChange,
                onAdd = onSave,
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.imePadding(),
            contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FoodHeadline(headline, Modifier.padding(horizontal = 8.dp))
            }
            if (image != null) {
                item {
                    FoodImage(image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            }
            if (components.isNotEmpty()) {
                item {
                    FoodIngredients(
                        components = components,
                        ingredientScalingFactor = ingredientScalingFactor,
                        onNavigateToIngredient = null,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
            }
            if (suggestions.isNotEmpty()) {
                item {
                    QuantitySuggestions(
                        suggestions = suggestions,
                        selectedType = selectedType,
                        formField = formField,
                        packageQuantity = packageQuantity,
                        servingQuantity = servingQuantity,
                        onSelectQuantity = {
                            onSelectQuantity(it)
                            formField.textFieldState.setTextAndPlaceCursorAtEnd(
                                it.amount.formatCompact()
                            )
                        },
                        modifier = Modifier.height(32.dp),
                    )
                }
            }
            item {
                DiaryInput(
                    selectedType = selectedType,
                    types = types,
                    formField = formField,
                    profiles = profiles,
                    selectedProfiles = selectedProfiles,
                    onSelectType = onSelectQuantityType,
                    onSelectProfiles = onSelectProfiles,
                    onKeyboardAction = { onSave(isTracked) },
                    modifier =
                        Modifier.focusRequester(focusRequester)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                )
            }
            if (scaledNutritionFacts != null) {
                item {
                    FoodDetailsNutrientsCompact(
                        scaledNutritionFacts = scaledNutritionFacts,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
        }
    }
}
