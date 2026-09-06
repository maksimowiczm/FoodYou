package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeExtendedFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.capabilities.fooddetails.FetchProgressIndicator
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodSourceLink
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsUiState
import com.maksimowiczm.foodyou.capabilities.fooddetails.fooddatacentral.FoodDataCentralDetailsViewModel
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.component.RefreshIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import foodyou.app.generated.resources.*
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddFoodDataCentralDiaryEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    mealId: MealId,
    id: FoodDataCentralProductId,
    initialQuantity: Quantity,
    date: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: FoodDataCentralDetailsViewModel = koinViewModel {
        parametersOf(id, initialQuantity)
    }
    val addFoodDiaryEntryViewModel: AddFoodDiaryEntryViewModel = koinViewModel {
        parametersOf(mealId)
    }

    LaunchedCollectWithLifecycle(addFoodDiaryEntryViewModel.createdUiEvent) {
        onAdd()
    }

    val foodUiState = foodViewModel.uiState.collectAsStateWithLifecycle().value
    val profiles = addFoodDiaryEntryViewModel.profiles.collectAsStateWithLifecycle().value
    val defaultProfileId =
        addFoodDiaryEntryViewModel.appProfileId.collectAsStateWithLifecycle().value

    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }
    val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)
    var selectedProfileIds by rememberSerializable { mutableStateOf(listOf(defaultProfileId)) }
    val selectedProfiles =
        remember(profiles, selectedProfileIds) {
            profiles?.filter { it.id in selectedProfileIds } ?: emptyList()
        }

    val details = foodUiState as? FoodDataCentralDetailsUiState.Details

    LaunchedEffect(formField.textFieldState.text, details?.selectedQuantityType) {
        foodViewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            details?.selectedQuantityType,
        )
    }

    val requiredState = if (profiles != null && details != null) profiles to details else null

    updateTransition(requiredState).Crossfade(contentKey = { it != null }) {
        if (it == null) LoadingScreen(onBack)
        else {
            val (profiles, details) = it
            AddFoodDataCentralDiaryEntryScreenContent(
                headline = details.food.headline,
                isFavorite = details.isFavorite,
                isLoading = details.isLoading,
                sourceUrl = details.food.source,
                suggestions = details.suggestions,
                scaledNutritionFacts = details.scaledNutritionFacts,
                packageQuantity = details.food.packageQuantity,
                servingQuantity = details.food.servingQuantity,
                types = details.quantityTypes,
                selectedType = details.selectedQuantityType,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                onBack = onBack,
                onAdd = {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val timestamp = LocalDateTime(date ?: now.date, now.time)

                    addFoodDiaryEntryViewModel.create(
                        product = details.food,
                        quantity = details.selectedQuantity,
                        profiles = selectedProfiles.map { profile -> profile.id },
                        timestamp = timestamp,
                    )
                },
                onRefresh = foodViewModel::refresh,
                onSetFavorite = foodViewModel::setFavorite,
                onSelectQuantity = foodViewModel::selectQuantity,
                onSelectQuantityType = foodViewModel::selectQuantityType,
                onSelectProfiles = { selectedProfiles ->
                    selectedProfileIds = selectedProfiles.map { profile -> profile.id }
                },
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun AddFoodDataCentralDiaryEntryScreenContent(
    headline: String?,
    isFavorite: Boolean,
    isLoading: Boolean,
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
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onAdd: () -> Unit,
    onSetFavorite: (Boolean) -> Unit,
    onSelectQuantity: (Quantity) -> Unit,
    onSelectQuantityType: (QuantityType) -> Unit,
    onSelectProfiles: (List<ProfileUiState>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberNutrientExpanded()
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
            LargeExtendedFloatingActionButton(
                onClick = onAdd,
                modifier =
                    Modifier.animateFloatingActionButton(
                        visible =
                            !isLoading &&
                                !LocalNavAnimatedContentScope.current.transition.isRunning &&
                                formField.error == null &&
                                selectedProfiles.isNotEmpty(),
                        alignment = Alignment.BottomEnd,
                    ),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                    modifier = Modifier.size(FloatingActionButtonDefaults.LargeIconSize),
                )
                Spacer(Modifier.width(16.dp))
                Text(stringResource(Res.string.action_add))
            }
        },
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
                    onKeyboardAction = onAdd,
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
            item {
                FoodSourceLink(
                    sourceUrl = sourceUrl,
                    logo = {
                        Image(
                            painter = painterResource(Res.drawable.usda_logo),
                            contentDescription = null,
                            modifier =
                                Modifier.sizeIn(
                                    maxHeight = 32.dp,
                                    maxWidth = 32.dp,
                                ),
                        )
                    },
                    headline = stringResource(Res.string.headline_fooddata_central),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                )
            }
        }
    }
}
