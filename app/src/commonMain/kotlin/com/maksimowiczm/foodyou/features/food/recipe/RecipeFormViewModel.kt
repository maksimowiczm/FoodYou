package com.maksimowiczm.foodyou.features.food.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.forceWeight
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class RecipeFormViewModel(
    initialIngredients: List<Pair<FoodCompositionComponentIdentity.Identified, Quantity>> =
        emptyList(),
    private val fdc: FoodDataCentralService,
    private val off: OpenFoodFactsService,
    private val up: UserProductService,
    private val recipeService: UserRecipeService,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val ingredients =
        MutableStateFlow(
            savedStateHandle.get<String>(INGREDIENTS_KEY)?.let {
                Json.decodeFromString<List<IngredientEntry>>(it)
            } ?: initialIngredients.map { (id, q) -> IngredientEntry(identity = id, quantity = q) }
        )

    private val componentFlows =
        mutableMapOf<Pair<FoodCompositionComponentIdentity, Quantity>, Flow<ResolvedIngredient?>>()

    val state: StateFlow<RecipeFormUiState> =
        ingredients
            .flatMapLatest { entries ->
                if (entries.isEmpty()) flowOf(RecipeFormUiState())
                else
                    entries
                        .map { entry ->
                            getComponentFlow(entry.identity, entry.quantity).map { resolved ->
                                IngredientItemState(entry, resolved)
                            }
                        }
                        .combine()
                        .map { items ->
                            val components = items.mapNotNull { it.resolved?.component }
                            RecipeFormUiState(
                                ingredients = items,
                                components =
                                    if (components.size == entries.size) components else null,
                            )
                        }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = RecipeFormUiState(),
            )

    init {
        ingredients
            .onEach { ingredients ->
                savedStateHandle[INGREDIENTS_KEY] = Json.encodeToString(ingredients)
                val activeKeys = ingredients.map { it.identity to it.quantity }.toSet()
                componentFlows.keys.retainAll(activeKeys)
            }
            .launchIn(viewModelScope)
    }

    private fun getComponentFlow(
        id: FoodCompositionComponentIdentity.Identified,
        quantity: Quantity,
    ): Flow<ResolvedIngredient?> {
        val key = id to quantity
        return componentFlows.getOrPut(key) {
            observeComponent(id, quantity)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = null,
                )
        }
    }

    fun removeIngredient(index: Int) {
        ingredients.update { current -> current.toMutableList().apply { removeAt(index) } }
    }

    fun addIngredient(identity: FoodCompositionComponentIdentity.Identified, quantity: Quantity) {
        ingredients.update { current ->
            current.toMutableList().apply {
                add(IngredientEntry(identity = identity, quantity = quantity))
            }
        }
    }

    fun updateIngredient(index: Int, quantity: Quantity) {
        ingredients.update { current ->
            current.toMutableList().apply { this[index] = this[index].copy(quantity = quantity) }
        }
    }

    private fun observeComponent(
        id: FoodCompositionComponentIdentity.Identified,
        quantity: Quantity,
    ): Flow<ResolvedIngredient?> =
        when (id) {
            is FoodCompositionComponentIdentity.Recipe ->
                recipeService.observe(UserRecipeIdentity(id.id)).map { recipe ->
                    recipe?.let {
                        ResolvedIngredient(
                            component =
                                FoodCompositionComponent.Composite(
                                    identity = id,
                                    name = it.name,
                                    image = it.image?.let(FoodCompositionComponentImage::Blob),
                                    quantity =
                                        FoodComponentQuantityUpdateService.map(
                                            quantity = quantity,
                                            servingWeight = recipe.servingWeight,
                                            packageWeight = recipe.totalWeight,
                                        ),
                                    components = it.components,
                                )
                        )
                    }
                }

            is FoodCompositionComponentIdentity.FoodDataCentral ->
                fdc.observeNullable(FoodDataCentralProductIdentity(id.fdcId)).map { product ->
                    product?.let {
                        ResolvedIngredient(
                            component =
                                FoodCompositionComponent.Simple(
                                    identity = id,
                                    name = FoodName(fallback = it.name),
                                    image = null,
                                    nutritionFacts = it.nutritionFacts,
                                    quantity =
                                        FoodComponentQuantityUpdateService.map(
                                            quantity = quantity,
                                            servingWeight = product.servingQuantity?.forceWeight(),
                                            packageWeight = product.packageQuantity?.forceWeight(),
                                        ),
                                )
                        )
                    }
                }

            is FoodCompositionComponentIdentity.OpenFoodFacts ->
                off.observeNullable(OpenFoodFactsProductIdentity(id.barcode)).map { product ->
                    product?.let {
                        ResolvedIngredient(
                            component =
                                FoodCompositionComponent.Simple(
                                    identity = id,
                                    name = it.name,
                                    image =
                                        (it.thumbnail ?: it.image)?.let(
                                            FoodCompositionComponentImage::Uri
                                        ),
                                    nutritionFacts = it.nutritionFacts,
                                    quantity =
                                        FoodComponentQuantityUpdateService.map(
                                            quantity = quantity,
                                            servingWeight = product.servingQuantity?.forceWeight(),
                                            packageWeight = product.packageQuantity?.forceWeight(),
                                        ),
                                )
                        )
                    }
                }

            is FoodCompositionComponentIdentity.UserProduct ->
                up.observe(UserProductIdentity(id.id)).map { product ->
                    product?.let {
                        ResolvedIngredient(
                            component =
                                FoodCompositionComponent.Simple(
                                    identity = id,
                                    name = it.name,
                                    image = it.image?.let(FoodCompositionComponentImage::Blob),
                                    nutritionFacts = it.nutritionFacts,
                                    quantity =
                                        FoodComponentQuantityUpdateService.map(
                                            quantity = quantity,
                                            servingWeight = product.servingQuantity?.forceWeight(),
                                            packageWeight = product.packageQuantity?.forceWeight(),
                                        ),
                                )
                        )
                    }
                }
        }

    private fun FoodDataCentralService.observeNullable(
        identity: FoodDataCentralProductIdentity
    ): Flow<FoodDataCentralProduct?> =
        observe(identity).map {
            when (it) {
                is RemoteData.Error<FoodDataCentralProduct> -> it.partialValue
                is RemoteData.Loading<FoodDataCentralProduct> -> it.partialValue
                RemoteData.NotFound -> null
                is RemoteData.Success<FoodDataCentralProduct> -> it.value
            }
        }

    private fun OpenFoodFactsService.observeNullable(
        identity: OpenFoodFactsProductIdentity
    ): Flow<OpenFoodFactsProduct?> =
        observe(identity).map {
            when (it) {
                is RemoteData.Error<OpenFoodFactsProduct> -> it.partialValue
                is RemoteData.Loading<OpenFoodFactsProduct> -> it.partialValue
                RemoteData.NotFound -> null
                is RemoteData.Success<OpenFoodFactsProduct> -> it.value
            }
        }

    companion object {
        private const val INGREDIENTS_KEY = "ingredients"
    }
}

data class RecipeFormUiState(
    val ingredients: List<IngredientItemState> = emptyList(),
    val components: List<FoodCompositionComponent>? = null,
)

@Serializable
data class IngredientEntry(
    val entryId: Uuid = Uuid.random(),
    val identity: FoodCompositionComponentIdentity.Identified,
    val quantity: Quantity,
)

data class ResolvedIngredient(val component: FoodCompositionComponent)

data class IngredientItemState(val entry: IngredientEntry, val resolved: ResolvedIngredient?)
