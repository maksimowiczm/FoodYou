package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientsExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition.SwissFoodCompositionDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.swissfoodcomposition.SwissFoodCompositionDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionProductId
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.swissFoodCompositionDatabaseUrl
import foodyou.app.generated.resources.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
context(animatedContentScope: AnimatedContentScope)
fun AddSwissFoodCompositionDiaryEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    mealId: MealId,
    id: SwissFoodCompositionProductId,
    initialQuantity: Quantity?,
    date: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: SwissFoodCompositionDetailsViewModel = koinViewModel {
        parametersOf(id, initialQuantity)
    }
    val addFoodDiaryEntryViewModel: AddFoodDiaryEntryViewModel = koinViewModel {
        parametersOf(mealId, date)
    }

    LaunchedCollectWithLifecycle(addFoodDiaryEntryViewModel.createdUiEvent) {
        onAdd()
    }

    val foodUiState = foodViewModel.uiState.collectAsStateWithLifecycle().value
    val diaryState = addFoodDiaryEntryViewModel.uiState.collectAsStateWithLifecycle().value

    val details = foodUiState as? SwissFoodCompositionDetailsUiState.Details
    val requiredState =
        if (details != null && diaryState is AddFoodDiaryEntryUiState.Ready) diaryState to details
        else null

    updateTransition(requiredState).Crossfade(contentKey = { it != null }) {
        if (it == null) LoadingScreen(onBack)
        else {
            val (diaryState, details) = it

            val selectedProfiles =
                remember(diaryState.profiles, diaryState.selectedProfileIds) {
                    diaryState.profiles.filter { it.id in diaryState.selectedProfileIds }
                }

            val defaultValue = remember { details.selectedQuantity.amount.formatCompact() }
            val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)
            LaunchedEffect(formField.textFieldState.text, details.selectedQuantityType) {
                foodViewModel.selectQuantity(
                    formField.textFieldState.text.toString().toDoubleOrNull(),
                    details.selectedQuantityType,
                )
            }

            val nameSelector = LocalFoodNameSelector.current
            val headline = nameSelector.select(details.food.name)

            AddSwissFoodCompositionDiaryEntryScreenContent(
                headline = headline,
                isFavorite = details.isFavorite,
                suggestions = details.suggestions,
                scaledNutritionFacts = details.scaledNutritionFacts,
                types = details.quantityTypes,
                selectedType = details.selectedQuantityType,
                formField = formField,
                profiles = diaryState.profiles,
                selectedProfiles = selectedProfiles,
                meals = diaryState.meals,
                selectedMeal = diaryState.selectedMeal,
                selectedDateTime = diaryState.selectedDateTime,
                onBack = onBack,
                onAdd = {
                    addFoodDiaryEntryViewModel.create(
                        product = details.food,
                        quantity = details.selectedQuantity,
                    )
                },
                onSetFavorite = foodViewModel::setFavorite,
                onSelectQuantity = foodViewModel::selectQuantity,
                onSelectQuantityType = foodViewModel::selectQuantityType,
                onSelectProfiles = {
                    addFoodDiaryEntryViewModel.selectProfiles(it.map { profile -> profile.id })
                },
                onSelectMeal = { addFoodDiaryEntryViewModel.selectMeal(it.id) },
                onSelectDate = addFoodDiaryEntryViewModel::selectDate,
                onSelectTime = addFoodDiaryEntryViewModel::selectTime,
                modifier = modifier,
            )
        }
    }
}

@Composable
context(animatedContentScope: AnimatedContentScope)
private fun AddSwissFoodCompositionDiaryEntryScreenContent(
    headline: String?,
    isFavorite: Boolean,
    suggestions: List<Quantity>,
    scaledNutritionFacts: NutritionFacts?,
    types: List<QuantityType>,
    selectedType: QuantityType,
    formField: FormField,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    meals: List<Meal>,
    selectedMeal: Meal,
    selectedDateTime: LocalDateTime,
    onBack: () -> Unit,
    onAdd: () -> Unit,
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
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            LargeExtendedFloatingActionButton(
                onClick = onAdd,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            !animatedContentScope.transition.isRunning &&
                                formField.error == null &&
                                selectedProfiles.isNotEmpty(),
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
        floatingActionButtonPosition = FabPosition.Center,
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.imePadding(),
            contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                FoodHeadline(headline, Modifier.padding(horizontal = 16.dp))
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
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                    if (suggestions.isNotEmpty()) {
                        QuantitySuggestions(
                            suggestions = suggestions,
                            selectedType = selectedType,
                            formField = formField,
                            packageQuantity = null,
                            servingQuantity = null,
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
                        onKeyboardAction = onAdd,
                        modifier =
                            Modifier.focusRequester(focusRequester)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                    )
                }
            }
            if (scaledNutritionFacts != null) {
                item {
                    FoodDetailsNutrientsCompact(
                        scaledNutritionFacts = scaledNutritionFacts,
                        expanded = expandingEnabled && expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
            item {
                FoodSourceLink(
                    sourceUrl = swissFoodCompositionDatabaseUrl,
                    headline = stringResource(Res.string.headline_swiss_food_composition),
                    logo = {},
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                )
            }
        }
    }
}
