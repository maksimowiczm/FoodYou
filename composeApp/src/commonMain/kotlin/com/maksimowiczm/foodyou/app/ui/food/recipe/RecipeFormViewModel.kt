package com.maksimowiczm.foodyou.app.ui.food.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.common.RemoteData
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodComposition
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentImage
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.PackageQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.ServingQuantity
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.common.domain.milliliters
import com.maksimowiczm.foodyou.common.extension.combine
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.fooddatacentral.application.FoodDataCentralService
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProduct
import com.maksimowiczm.foodyou.fooddatacentral.domain.FoodDataCentralProductIdentity
import com.maksimowiczm.foodyou.openfoodfacts.application.OpenFoodFactsService
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProduct
import com.maksimowiczm.foodyou.openfoodfacts.domain.OpenFoodFactsProductIdentity
import com.maksimowiczm.foodyou.userproduct.application.UserProductService
import com.maksimowiczm.foodyou.userproduct.domain.UserProduct
import com.maksimowiczm.foodyou.userproduct.domain.UserProductIdentity
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipe
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
    initialIngredients: List<Pair<FoodCompositionComponentIdentity, Quantity>> = emptyList(),
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
                                composition =
                                    if (components.size == entries.size) FoodComposition(components)
                                    else null,
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
        id: FoodCompositionComponentIdentity,
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

    fun addIngredient(identity: FoodCompositionComponentIdentity, quantity: Quantity) {
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
        id: FoodCompositionComponentIdentity,
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
                                    quantity = it.toComponentQuantity(quantity),
                                    composition = it.composition,
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
                                    quantity = it.toComponentQuantity(quantity),
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
                                    quantity = it.toComponentQuantity(quantity),
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
                                    quantity = it.toComponentQuantity(quantity),
                                )
                        )
                    }
                }
        }

    private fun UserRecipe.toComponentQuantity(quantity: Quantity): FoodComponentComponentQuantity =
        resolveComponentQuantity(
            quantity = quantity,
            packageQuantity = AbsoluteQuantity.Weight(totalWeight),
            servingQuantity = AbsoluteQuantity.Weight(servingWeight),
        )

    private fun FoodDataCentralProduct.toComponentQuantity(
        quantity: Quantity
    ): FoodComponentComponentQuantity =
        resolveComponentQuantity(
            quantity = quantity,
            packageQuantity = packageQuantity,
            servingQuantity = servingQuantity,
        )

    private fun OpenFoodFactsProduct.toComponentQuantity(
        quantity: Quantity
    ): FoodComponentComponentQuantity =
        resolveComponentQuantity(
            quantity = quantity,
            packageQuantity = packageQuantity,
            servingQuantity = servingQuantity,
        )

    private fun UserProduct.toComponentQuantity(
        quantity: Quantity
    ): FoodComponentComponentQuantity =
        resolveComponentQuantity(
            quantity = quantity,
            packageQuantity = packageQuantity,
            servingQuantity = servingQuantity,
            isLiquid = isLiquid,
        )

    private fun resolveComponentQuantity(
        quantity: Quantity,
        packageQuantity: AbsoluteQuantity?,
        servingQuantity: AbsoluteQuantity?,
        isLiquid: Boolean = false,
    ): FoodComponentComponentQuantity {
        val absolute =
            QuantityCalculator.calculateAbsoluteQuantity(
                    suggestedQuantity = quantity,
                    packageQuantity = packageQuantity,
                    servingQuantity = servingQuantity,
                )
                .getOrNull()
                ?: packageQuantity
                ?: servingQuantity
                ?: if (isLiquid) AbsoluteQuantity.Volume(100.milliliters)
                else AbsoluteQuantity.Weight(100.grams)

        val weight =
            when (absolute) {
                is AbsoluteQuantity.Weight -> absolute.weight
                is AbsoluteQuantity.Volume -> absolute.volume.milliliters.grams
            }

        return when (quantity) {
            is AbsoluteQuantity -> FoodComponentComponentQuantity.Weight(weight)
            is PackageQuantity -> {
                val pw =
                    when (packageQuantity) {
                        is AbsoluteQuantity.Weight -> packageQuantity.weight
                        is AbsoluteQuantity.Volume -> packageQuantity.volume.milliliters.grams
                        null -> weight / quantity.packages
                    }
                FoodComponentComponentQuantity.Package(quantity.packages, pw)
            }

            is ServingQuantity -> {
                val sw =
                    when (servingQuantity) {
                        is AbsoluteQuantity.Weight -> servingQuantity.weight
                        is AbsoluteQuantity.Volume -> servingQuantity.volume.milliliters.grams
                        null -> weight / quantity.servings
                    }
                FoodComponentComponentQuantity.Serving(quantity.servings, sw)
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
    val composition: FoodComposition? = null,
)

@Serializable
data class IngredientEntry(
    val entryId: Uuid = Uuid.random(),
    val identity: FoodCompositionComponentIdentity,
    val quantity: Quantity,
)

data class ResolvedIngredient(val component: FoodCompositionComponent)

data class IngredientItemState(val entry: IngredientEntry, val resolved: ResolvedIngredient?)
