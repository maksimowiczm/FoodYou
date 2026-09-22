package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FetchProgressIndicator
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.openfoodfacts.OpenFoodFactsDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientsExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntry
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.component.RefreshIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
context(animatedContentScope: AnimatedContentScope)
fun UpdateOpenFoodFactsDiaryEntryScreen(
    onBack: () -> Unit,
    onSave: (Quantity, Boolean) -> Unit,
    foodId: OpenFoodFactsProductId,
    entry: FoodDiaryEntry,
    profiles: List<ProfileUiState>,
    meals: List<Meal>,
    selectedMeal: Meal,
    selectedDateTime: LocalDateTime,
    selectedProfileIds: List<ProfileId>,
    onSelectMeal: (Meal) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectTime: (LocalTime) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: OpenFoodFactsDetailsViewModel = koinViewModel {
        parametersOf(foodId, entry.snapshot.quantity.toQuantity())
    }

    val foodUiState = foodViewModel.uiState.collectAsStateWithLifecycle().value

    val defaultValue =
        remember(entry.snapshot.quantity) {
            entry.snapshot.quantity.toQuantity().amount.formatCompact()
        }
    val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)
    val selectedProfiles =
        remember(profiles, selectedProfileIds) {
            profiles.filter { it.id in selectedProfileIds }
        }
    var isTracked by rememberSaveable {
        mutableStateOf(entry.snapshot.id !is FoodSnapshotId.Anonymous)
    }

    val nameSelector = LocalFoodNameSelector.current
    val details = foodUiState as? OpenFoodFactsDetailsUiState.Details

    LaunchedEffect(formField.textFieldState.text, details?.selectedQuantityType) {
        foodViewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            details?.selectedQuantityType,
        )
    }

    updateTransition(details).Crossfade(contentKey = { it != null }) {
        if (it == null) LoadingScreen(onBack)
        else
            UpdateOpenFoodFactsDiaryEntryScreenContent(
                headline = it.food.headline(nameSelector),
                isFavorite = it.isFavorite,
                isLoading = it.isLoading,
                image = it.food.image,
                sourceUrl = it.food.source,
                suggestions = it.suggestions,
                scaledNutritionFacts = it.scaledNutritionFacts,
                packageQuantity = it.food.packageQuantity,
                servingQuantity = it.food.servingQuantity,
                types = it.quantityTypes,
                selectedType = it.selectedQuantityType,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                meals = meals,
                selectedMeal = selectedMeal,
                selectedDateTime = selectedDateTime,
                isTracked = isTracked,
                onIsTrackedChange = { isTracked = it },
                onBack = onBack,
                onSave = { trackFood -> onSave(it.selectedQuantity, trackFood) },
                onRefresh = foodViewModel::refresh,
                onSetFavorite = foodViewModel::setFavorite,
                onSelectQuantity = foodViewModel::selectQuantity,
                onSelectQuantityType = foodViewModel::selectQuantityType,
                onSelectProfiles = onSelectProfiles,
                onSelectMeal = onSelectMeal,
                onSelectDate = onSelectDate,
                onSelectTime = onSelectTime,
                modifier = modifier,
            )
    }
}

@Composable
context(animatedContentScope: AnimatedContentScope)
private fun UpdateOpenFoodFactsDiaryEntryScreenContent(
    headline: String?,
    isFavorite: Boolean,
    isLoading: Boolean,
    image: FileUri?,
    sourceUrl: String,
    suggestions: List<Quantity>,
    scaledNutritionFacts: NutritionFacts?,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    types: List<QuantityType>,
    selectedType: QuantityType,
    formField: FormField,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    meals: List<Meal>,
    selectedMeal: Meal,
    selectedDateTime: LocalDateTime,
    isTracked: Boolean,
    onIsTrackedChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onSave: (trackFood: Boolean) -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    onSelectMeal: (Meal) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onSelectTime: (LocalTime) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientsExpanded()
    val expandingEnabled =
        remember(scaledNutritionFacts) {
            val scaledNutritionFacts = scaledNutritionFacts ?: return@remember false
            (Nutrient.all - Nutrient.basic).any { scaledNutritionFacts[it].value != null }
        }

    val focusRequester = remember { FocusRequester() }
    var focusRequested by rememberSaveable { mutableStateOf(false) }
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
                actions = {
                    FavoriteIconButton(isFavorite = isFavorite, onChange = onSetFavorite)
                    RefreshIconButton(onRefresh = onRefresh)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            AddFoodDiaryEntryFab(
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            !isLoading &&
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
        FetchProgressIndicator(
            isLoading = isLoading,
            modifier = Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f),
        )
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
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DiaryDateTimePicker(
                        meals = meals,
                        selectedMeal = selectedMeal,
                        selectedDateTime = selectedDateTime,
                        onMealChange = onSelectMeal,
                        onDateChange = onSelectDate,
                        onTimeChange = onSelectTime,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                    if (suggestions.isNotEmpty()) {
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
            item {
                FoodSourceLink(
                    sourceUrl = sourceUrl,
                    logo = {
                        Image(
                            painter = painterResource(Res.drawable.openfoodfacts_logo),
                            contentDescription = null,
                            modifier =
                                Modifier.sizeIn(
                                    maxHeight = 32.dp,
                                    maxWidth = 32.dp,
                                ),
                        )
                    },
                    headline = stringResource(Res.string.headline_open_food_facts),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }
    }
}
