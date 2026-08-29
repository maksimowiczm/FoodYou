package com.maksimowiczm.foodyou.features.diary

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
import com.maksimowiczm.foodyou.common.domain.food.Nutrient
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.amount
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
import com.maksimowiczm.foodyou.shared.ui.component.FavoriteIconButton
import com.maksimowiczm.foodyou.shared.ui.extension.LaunchedCollectWithLifecycle
import com.maksimowiczm.foodyou.shared.ui.extension.add
import com.maksimowiczm.foodyou.shared.ui.form.FormField
import com.maksimowiczm.foodyou.shared.ui.utility.LocalFoodNameSelector
import com.maksimowiczm.foodyou.shared.ui.utility.formatCompact
import com.maksimowiczm.foodyou.shared.ui.utility.headline
import com.maksimowiczm.foodyou.shared.ui.utility.resolveBlob
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.action_add
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AddUserProductDiaryEntryScreen(
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    mealIdentity: MealIdentity,
    identity: UserProductIdentity,
    initialQuantity: Quantity,
    date: LocalDate?,
    modifier: Modifier = Modifier,
) {
    val foodViewModel: UserProductDetailsViewModel = koinViewModel {
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

    LaunchedCollectWithLifecycle(foodViewModel.uiEvents) {
        when (it) {
            UserProductDetailsUiEvent.Deleted -> onDelete()
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

    val nameSelector = LocalFoodNameSelector.current
    val image = foodUiState.product?.image?.let { resolveBlob(it) }

    val scope =
        remember(
            foodUiState,
            formField,
            profiles,
            selectedProfiles,
            nameSelector,
            image,
        ) {
            val profiles = profiles ?: return@remember null
            val product = foodUiState.product ?: return@remember null

            AddUserProductDiaryEntryScope(
                headline = product.headline(nameSelector),
                isFavorite = foodUiState.isFavorite,
                image = image,
                suggestions = foodUiState.suggestions,
                selectedQuantity = foodUiState.selectedQuantity,
                scaledNutritionFacts = foodUiState.scaledNutritionFacts,
                types = foodUiState.quantityTypes,
                selectedType = foodUiState.selectedQuantityType ?: QuantityType.Gram,
                formField = formField,
                profiles = profiles,
                selectedProfiles = selectedProfiles,
                product = product,
            )
        }

    LaunchedEffect(formField.textFieldState.text, scope?.selectedType) {
        foodViewModel.selectQuantity(
            formField.textFieldState.text.toString().toDoubleOrNull(),
            scope?.selectedType,
        )
    }

    scope?.let {
        AddUserProductDiaryEntryScreenContent(
            scope = it,
            onBack = onBack,
            onAdd = {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                val timestamp = LocalDateTime(date ?: now.date, now.time)

                foodDiaryEntryViewModel.create(
                    product = it.product,
                    quantity = it.selectedQuantity ?: return@AddUserProductDiaryEntryScreenContent,
                    profiles = it.selectedProfiles.map { profile -> profile.id },
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

@Immutable
private data class AddUserProductDiaryEntryScope(
    val headline: String?,
    val isFavorite: Boolean,
    val image: FileUri?,
    val suggestions: List<Quantity>,
    val selectedQuantity: Quantity?,
    val scaledNutritionFacts: NutritionFacts?,
    val types: List<QuantityType>,
    val selectedType: QuantityType,
    val formField: FormField,
    val profiles: List<ProfileUiState>,
    val selectedProfiles: List<ProfileUiState>,
    val product: UserProduct,
) {
    val packageQuantity = product.packageQuantity
    val servingQuantity = product.servingQuantity
    val note = product.note
}

@Composable
private fun AddUserProductDiaryEntryScreenContent(
    scope: AddUserProductDiaryEntryScope,
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
        remember(scope.scaledNutritionFacts) {
            val scaledNutritionFacts = scope.scaledNutritionFacts ?: return@remember false
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
                title = scope.headline,
                actions = {
                    FavoriteIconButton(
                        isFavorite = scope.isFavorite,
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
                                scope.formField.error == null &&
                                scope.selectedProfiles.isNotEmpty(),
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
                FoodHeadline(scope.headline, Modifier.padding(horizontal = 8.dp))
            }
            if (scope.image != null) {
                item {
                    FoodImage(scope.image, Modifier.fillMaxWidth().padding(horizontal = 8.dp))
                }
            }
            if (scope.suggestions.isNotEmpty()) {
                item {
                    QuantitySuggestions(
                        suggestions = scope.suggestions,
                        selectedType = scope.selectedType,
                        formField = scope.formField,
                        packageQuantity = scope.packageQuantity,
                        servingQuantity = scope.servingQuantity,
                        onSelectQuantity = {
                            onSelectQuantity(it)
                            scope.formField.textFieldState.setTextAndPlaceCursorAtEnd(
                                it.amount.formatCompact()
                            )
                        },
                        modifier = Modifier.height(32.dp),
                    )
                }
            }
            item {
                DiaryInput(
                    selectedType = scope.selectedType,
                    types = scope.types,
                    formField = scope.formField,
                    profiles = scope.profiles,
                    selectedProfiles = scope.selectedProfiles,
                    onSelectType = onSelectQuantityType,
                    onSelectProfiles = onSelectProfiles,
                    modifier =
                        Modifier.focusRequester(focusRequester)
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                )
            }
            if (scope.scaledNutritionFacts != null) {
                item {
                    FoodDetailsNutrientsCompact(
                        scaledNutritionFacts = scope.scaledNutritionFacts,
                        expanded = if (!expandingEnabled) false else expanded,
                        onExpandedChange = { expanded = it },
                        expandingEnabled = expandingEnabled,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
            if (scope.note != null) {
                item {
                    FoodNote(scope.note, Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}
