package com.maksimowiczm.foodyou.features.diary

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Arrangement
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsNutrientsCompact
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodHeadline
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodImage
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodNote
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodScreenTopBar
import com.maksimowiczm.foodyou.capabilities.fooddetails.QuantitySuggestions
import com.maksimowiczm.foodyou.capabilities.fooddetails.UserFoodMenu
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberNutrientExpanded
import com.maksimowiczm.foodyou.capabilities.fooddetails.rememberQuantityFormField
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsUiEvent
import com.maksimowiczm.foodyou.capabilities.fooddetails.userproduct.UserProductDetailsViewModel
import com.maksimowiczm.foodyou.common.domain.FileUri
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.component.LoadingScreen
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import foodyou.app.generated.resources.*
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddUserProductDiaryEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    mealId: MealId,
    id: UserProductId,
    initialQuantity: Quantity,
    date: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: UserProductDetailsViewModel = koinViewModel {
        parametersOf(id, initialQuantity)
    }
    val addFoodDiaryEntryViewModel: AddFoodDiaryEntryViewModel = koinViewModel {
        parametersOf(mealId)
    }

    LaunchedCollectWithLifecycle(addFoodDiaryEntryViewModel.createdUiEvent) {
        onAdd()
    }

    LaunchedCollectWithLifecycle(foodViewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
        }
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

    val nameSelector = LocalFoodNameSelector.current
    val image = foodUiState.product?.image?.let { resolveBlob(it) }

    val selectedType = foodUiState.selectedQuantityType ?: QuantityType.Gram

    LaunchedEffect(formField.textFieldState.text, selectedType) {
        foodViewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            selectedType,
        )
    }

    val requiredState =
        if (profiles != null && foodUiState.product != null) profiles to foodUiState.product
        else null

    updateTransition(requiredState).Crossfade(contentKey = { it != null }) {
        if (it == null) LoadingScreen(onBack)
        else {
            val (profiles, product) = it
            AddUserProductDiaryEntryScreenContent(
                headline = product.headline(nameSelector),
                isFavorite = foodUiState.isFavorite,
                image = image,
                suggestions = foodUiState.suggestions,
                scaledNutritionFacts = foodUiState.scaledNutritionFacts,
                types = foodUiState.quantityTypes,
                selectedType = selectedType,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                packageQuantity = product.packageQuantity,
                servingQuantity = product.servingQuantity,
                note = product.note,
                onBack = onBack,
                onAdd = {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                    val timestamp = LocalDateTime(date ?: now.date, now.time)

                    addFoodDiaryEntryViewModel.create(
                        product = product,
                        quantity =
                            foodUiState.selectedQuantity
                                ?: return@AddUserProductDiaryEntryScreenContent,
                        profiles = selectedProfiles.map { profile -> profile.id },
                        timestamp = timestamp,
                    )
                },
                onEdit = onEdit,
                onDelete = foodViewModel::delete,
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
private fun AddUserProductDiaryEntryScreenContent(
    headline: String?,
    isFavorite: Boolean,
    image: FileUri?,
    suggestions: List<Quantity>,
    scaledNutritionFacts: NutritionFacts?,
    types: List<QuantityType>,
    selectedType: QuantityType,
    formField: FormField,
    profiles: List<ProfileUiState>,
    selectedProfiles: List<ProfileUiState>,
    packageQuantity: AbsoluteQuantity?,
    servingQuantity: AbsoluteQuantity?,
    note: String?,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
                    FavoriteIconButton(
                        isFavorite = isFavorite,
                        onChange = onSetFavorite,
                    )
                    UserFoodMenu(onEdit = onEdit, onDelete = onDelete)
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
            if (note != null) {
                item {
                    FoodNote(note, Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}
