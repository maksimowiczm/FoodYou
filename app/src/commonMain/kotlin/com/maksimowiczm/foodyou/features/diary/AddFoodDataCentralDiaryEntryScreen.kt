package com.maksimowiczm.foodyou.features.diary

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
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodUiScope
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodUiScopeWithFetch
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodUiScopeWithSource
import com.maksimowiczm.foodyou.capabilities.fooddetails.Headline
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.SourceLink
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
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
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

@Composable
fun AddFoodDataCentralDiaryEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    mealIdentity: MealIdentity,
    identity: FoodDataCentralProductIdentity,
    initialQuantity: Quantity,
    date: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: FoodDataCentralDetailsViewModel = koinViewModel {
        parametersOf(identity, initialQuantity)
    }
    val foodDiaryEntryViewModel: FoodDiaryEntryViewModel = koinViewModel {
        parametersOf(mealIdentity)
    }

    LaunchedCollectWithLifecycle(foodDiaryEntryViewModel.uiEvents) {
        when (it) {
            is FoodDiaryUiEvents.Created -> onAdd()
        }
    }

    val foodUiState = foodViewModel.uiState.collectAsStateWithLifecycle().value
    val profiles = foodDiaryEntryViewModel.profiles.collectAsStateWithLifecycle().value
    val defaultProfileId = foodDiaryEntryViewModel.appProfileId.collectAsStateWithLifecycle().value

    val defaultValue = remember(initialQuantity) { initialQuantity.amount.formatCompact() }
    val formField = rememberQuantityFormField(defaultValue, defaultValue = defaultValue)
    var selectedProfileIds by rememberSerializable { mutableStateOf(listOf(defaultProfileId)) }
    val selectedProfiles =
        remember(profiles, selectedProfileIds) {
            profiles?.filter { it.id in selectedProfileIds } ?: emptyList()
        }

    val scope =
        remember(
            foodUiState,
            formField,
            profiles,
            selectedProfiles,
        ) {
            val profiles = profiles ?: return@remember null
            val details =
                foodUiState as? FoodDataCentralDetailsUiState.Details ?: return@remember null
            val food = details.food

            AddFoodDataCentralDiaryEntryScope(
                headline = food.headline,
                isFavorite = details.isFavorite,
                isLoading = details.isLoading,
                sourceUrl = food.source,
                suggestions = details.suggestions,
                selectedQuantity = details.selectedQuantity,
                scaledNutritionFacts = details.scaledNutritionFacts,
                packageQuantity = food.packageQuantity,
                servingQuantity = food.servingQuantity,
                types = details.quantityTypes,
                selectedType = details.selectedQuantityType,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                product = food,
            )
        }

    LaunchedEffect(formField.textFieldState.text, scope?.selectedType) {
        foodViewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            scope?.selectedType,
        )
    }

    scope?.Screen(
        onBack = onBack,
        onAdd = {
            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val timestamp = LocalDateTime(date ?: now.date, now.time)

            foodDiaryEntryViewModel.create(
                product = scope.product,
                quantity = scope.selectedQuantity ?: return@Screen,
                profiles = scope.selectedProfiles.map { it.id },
                timestamp = timestamp,
            )
        },
        onRefresh = foodViewModel::refresh,
        onSetFavorite = foodViewModel::setFavorite,
        onSelectQuantity = foodViewModel::selectQuantity,
        onSelectQuantityType = foodViewModel::selectQuantityType,
        onSelectProfiles = { selectedProfileIds = it.map { it.id } },
        modifier = modifier,
    )
}

@Immutable
private data class AddFoodDataCentralDiaryEntryScope(
    override val headline: String?,
    override val isFavorite: Boolean,
    override val isLoading: Boolean,
    override val sourceUrl: String,
    override val suggestions: List<Quantity>,
    override val selectedQuantity: Quantity?,
    override val scaledNutritionFacts: NutritionFacts?,
    override val packageQuantity: AbsoluteQuantity?,
    override val servingQuantity: AbsoluteQuantity?,
    override val types: List<QuantityType>,
    override val selectedType: QuantityType,
    override val formField: FormField,
    override val profiles: List<ProfileUiState>,
    override val selectedProfiles: List<ProfileUiState>,
    val product: FoodDataCentralProduct,
) : FoodUiScope, FoodUiScopeWithFetch, FoodUiScopeWithSource, FoodUiScopeWithDiaryInput

@Composable
private fun AddFoodDataCentralDiaryEntryScope.Screen(
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
            Modifier.padding(top = contentPadding.calculateTopPadding()).zIndex(100f)
        )
        LazyColumn(
            modifier = Modifier.imePadding(),
            contentPadding = contentPadding.add(top = 26.dp, bottom = 128.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Headline(Modifier.padding(horizontal = 8.dp))
            }
            if (suggestions.isNotEmpty()) {
                item {
                    QuantitySuggestions(
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
                    onSelectType = onSelectQuantityType,
                    onSelectProfiles = onSelectProfiles,
                    modifier =
                        Modifier.focusRequester(focusRequester)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                )
            }
            if (scaledNutritionFacts != null) {
                item {
                    FoodDetailsNutrientsCompact(
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
            item {
                SourceLink(
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
