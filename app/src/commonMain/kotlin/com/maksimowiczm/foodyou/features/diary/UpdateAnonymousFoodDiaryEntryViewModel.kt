package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsQuantityDelegate
import com.maksimowiczm.foodyou.common.domain.food.CompositeFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.domain.food.toAbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.domain.food.totalWeight
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductId
import com.maksimowiczm.foodyou.fooddatacentral.domain.toSnapshot
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductId
import com.maksimowiczm.foodyou.openfoodfacts.domain.toSnapshot
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import com.maksimowiczm.foodyou.userproduct.domain.toSnapshot
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeId
import com.maksimowiczm.foodyou.userrecipe.domain.toSnapshot
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class UpdateAnonymousFoodDiaryEntryViewModel(
    private val measuredSnapshot: MeasuredFoodSnapshot,
    fdc: FoodDataCentralService,
    off: OpenFoodFactsService,
    up: UserProductService,
    recipeService: UserRecipeService,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val snapshot = measuredSnapshot.snapshot
    private val initialQuantity = measuredSnapshot.quantity.toQuantity()

    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    private val isTracked = MutableStateFlow(savedStateHandle.get<Boolean>(IS_TRACKED_KEY) ?: false)

    private val trackedMeasuredSnapshot =
        (snapshot.id as? FoodSnapshotId.Anonymous)?.trackedId?.let { id ->
            when (id) {
                is FoodSnapshotId.FoodDataCentral ->
                    fdc.observe(FoodDataCentralProductId(id.fdcId)).map { data ->
                        data.getOrNull()?.let { product ->
                            val snapshot = product.toSnapshot()
                            MeasuredFoodSnapshot(
                                snapshot = snapshot,
                                quantity =
                                    FoodSnapshotQuantityUpdateService.map(
                                        quantity = initialQuantity,
                                        servingWeight = product.servingQuantity?.forceWeight(),
                                        packageWeight = product.packageQuantity?.forceWeight(),
                                    ),
                            )
                        }
                    }
                is FoodSnapshotId.OpenFoodFacts ->
                    off.observe(OpenFoodFactsProductId(id.barcode)).map { data ->
                        data.getOrNull()?.let { product ->
                            val snapshot = product.toSnapshot()
                            MeasuredFoodSnapshot(
                                snapshot = snapshot,
                                quantity =
                                    FoodSnapshotQuantityUpdateService.map(
                                        quantity = initialQuantity,
                                        servingWeight = product.servingQuantity?.forceWeight(),
                                        packageWeight = product.packageQuantity?.forceWeight(),
                                    ),
                            )
                        }
                    }
                is FoodSnapshotId.UserProduct ->
                    up.observe(UserProductId(id.id)).map { product ->
                        product?.let {
                            val snapshot = it.toSnapshot()
                            MeasuredFoodSnapshot(
                                snapshot = snapshot,
                                quantity =
                                    FoodSnapshotQuantityUpdateService.map(
                                        quantity = initialQuantity,
                                        servingWeight = it.servingQuantity?.forceWeight(),
                                        packageWeight = it.packageQuantity?.forceWeight(),
                                    ),
                            )
                        }
                    }
                is FoodSnapshotId.UserRecipe ->
                    recipeService.observe(UserRecipeId(id.id)).map { recipe ->
                        recipe?.let {
                            val snapshot = it.toSnapshot()
                            MeasuredFoodSnapshot(
                                snapshot = snapshot,
                                quantity =
                                    FoodSnapshotQuantityUpdateService.map(
                                        quantity = initialQuantity,
                                        servingWeight = it.servingWeight,
                                        packageWeight = it.totalWeight,
                                    ),
                            )
                        }
                    }
            }
        } ?: flowOf(null)

    val uiState =
        combine(
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
                isTracked,
                trackedMeasuredSnapshot,
            ) { selectedQuantityType, selectedQuantity, isTracked, trackedMeasuredSnapshot ->
                val activeMeasuredSnapshot =
                    if (isTracked) trackedMeasuredSnapshot ?: measuredSnapshot else measuredSnapshot
                val activeSnapshot = activeMeasuredSnapshot.snapshot

                val servingQuantity =
                    activeMeasuredSnapshot.quantity.servingWeight?.toAbsoluteQuantity()
                val packageQuantity =
                    activeMeasuredSnapshot.quantity.packageWeight?.toAbsoluteQuantity()

                val result =
                    quantityDelegate.calculate(
                        product =
                            FoodDetailsQuantityDelegate.QuantityProvider(
                                servingQuantity = servingQuantity,
                                packageQuantity = packageQuantity,
                                nutritionFacts = activeSnapshot.nutritionFacts,
                                isLiquid = false, // TODO We don't have this info for anonymous food
                                // currently
                            ),
                        currentSelectedQuantity = selectedQuantity,
                        currentSelectedQuantityType = selectedQuantityType,
                    )

                UpdateAnonymousFoodDiaryEntryUiState.Loaded(
                    snapshot = activeSnapshot,
                    suggestions = result.suggestions,
                    selectedQuantity = result.selectedQuantity,
                    scaledNutritionFacts = result.scaledNutritionFacts,
                    quantityTypes = result.quantityTypes,
                    selectedQuantityType = result.selectedQuantityType,
                    ingredientScalingFactor =
                        if (activeSnapshot is CompositeFoodSnapshot) {
                            val totalWeight = activeSnapshot.components.totalWeight
                            if (totalWeight.grams > 0.0) {
                                val selectedAbsoluteQuantity =
                                    QuantityCalculator.calculateAbsoluteQuantity(
                                            suggestedQuantity = result.selectedQuantity,
                                            packageQuantity = packageQuantity,
                                            servingQuantity = servingQuantity,
                                        )
                                        .getOrNull()

                                (selectedAbsoluteQuantity?.forceWeight() ?: totalWeight) /
                                    totalWeight
                            } else 1.0
                        } else 1.0,
                    isTracked = isTracked,
                    relinkedSnapshot =
                        activeMeasuredSnapshot.copy(
                            quantity =
                                FoodSnapshotQuantityUpdateService.map(
                                    quantity = result.selectedQuantity,
                                    servingWeight = activeMeasuredSnapshot.quantity.servingWeight,
                                    packageWeight = activeMeasuredSnapshot.quantity.packageWeight,
                                )
                        ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = UpdateAnonymousFoodDiaryEntryUiState.Loading,
            )

    fun selectQuantity(quantity: Quantity) {
        quantityDelegate.selectQuantity(quantity)
    }

    fun selectQuantity(amount: Double?, type: QuantityType?) {
        if ((amount != null) && (amount > 0.0) && (type != null)) {
            selectQuantity(type.toQuantity(amount))
        }
    }

    fun selectQuantityType(type: QuantityType) {
        quantityDelegate.selectQuantityType(type)
    }

    fun setIsTracked(isTracked: Boolean) {
        this.isTracked.value = isTracked
        savedStateHandle[IS_TRACKED_KEY] = isTracked
    }

    companion object {
        private const val IS_TRACKED_KEY = "is_tracked"
    }
}
