package com.maksimowiczm.foodyou.feature.recipe.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.maksimowiczm.foodyou.core.ext.combine
import com.maksimowiczm.foodyou.core.model.FoodId
import com.maksimowiczm.foodyou.core.model.Measurement
import com.maksimowiczm.foodyou.core.model.Product
import com.maksimowiczm.foodyou.core.repository.FoodRepository
import com.maksimowiczm.foodyou.core.repository.SearchRepository
import com.maksimowiczm.foodyou.feature.measurement.ObserveMeasurableFoodUseCase
import com.maksimowiczm.foodyou.feature.recipe.data.RecipeRepository
import com.maksimowiczm.foodyou.feature.recipe.model.Ingredient
import com.maksimowiczm.foodyou.feature.recipe.model.compare
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pro.respawn.kmmutils.inputforms.Form
import pro.respawn.kmmutils.inputforms.ValidationStrategy
import pro.respawn.kmmutils.inputforms.default.Rules
import pro.respawn.kmmutils.inputforms.dsl.input
import pro.respawn.kmmutils.inputforms.dsl.isValid

private data class IngredientInternal(val measurement: Measurement, val productId: FoodId.Product)

internal class RecipeViewModel(
    private val foodRepository: FoodRepository,
    searchRepository: SearchRepository,
    private val recipeRepository: RecipeRepository,
    private val observeMeasurableFoodUseCase: ObserveMeasurableFoodUseCase,
    private val recipeId: Long = -1
) : ViewModel() {
    private val recipe = runBlocking { recipeRepository.getRecipeById(recipeId) }
    private val action = if (recipe == null) {
        RecipeAction.Create
    } else {
        RecipeAction.Update
    }

    private val _ingredients = MutableStateFlow<List<IngredientInternal>>(
        recipe?.ingredients?.map {
            IngredientInternal(
                measurement = it.measurement,
                productId = it.product.id
            )
        } ?: emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val ingredients = _ingredients.flatMapLatest {
        if (it.isEmpty()) {
            return@flatMapLatest flowOf(emptyList<Ingredient>())
        }

        it.map {
            foodRepository
                .observeFood(it.productId)
                .filterNotNull()
                .map { product ->
                    product as Product

                    Ingredient(
                        product = product,
                        measurement = it.measurement
                    )
                }
        }.combine { it.toList() }
    }

    private val nameState = MutableStateFlow(recipe?.name?.let { input(it) } ?: input())
    private val servingsState =
        MutableStateFlow(recipe?.servings?.let { input(it.toString()) } ?: input("1"))

    val state = kotlinx.coroutines.flow.combine(
        nameState,
        servingsState,
        ingredients
    ) { name, servings, ingredients ->
        RecipeState(
            name = name,
            servings = servings,
            isModified = if (recipe == null) {
                name.value.isNotBlank() || servings.value != "1" || ingredients.isNotEmpty()
            } else {
                name.value != recipe.name ||
                    servings.value != recipe.servings.toString() ||
                    !ingredients.compare(recipe.ingredients)
            },
            ingredients = ingredients,
            action = action
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = RecipeState(action = action)
    )

    val recentQueries = searchRepository.observeRecentQueries(20).stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(30_000L),
        initialValue = emptyList()
    )

    private val _searchQuery = MutableSharedFlow<String?>(replay = 1).apply { tryEmit(null) }
    val searchQuery = _searchQuery.asSharedFlow()

    fun onSearch(query: String?) {
        _searchQuery.tryEmit(query)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pages = _searchQuery.flatMapLatest { query ->
        recipeRepository.queryProducts(query)
    }.cachedIn(viewModelScope)

    fun observeMeasurableFood(productId: Long) =
        observeMeasurableFoodUseCase(FoodId.Product(productId))

    fun onAddIngredient(ingredient: Ingredient) {
        _ingredients.update {
            it + IngredientInternal(
                measurement = ingredient.measurement,
                productId = ingredient.product.id
            )
        }
    }

    fun onUpdateIngredient(index: Int, ingredient: Ingredient) {
        _ingredients.update {
            it.mapIndexed { i, item ->
                if (i == index) {
                    IngredientInternal(
                        measurement = ingredient.measurement,
                        productId = ingredient.product.id
                    )
                } else {
                    item
                }
            }
        }
    }

    fun onRemoveIngredient(index: Int) {
        _ingredients.update {
            it.filterIndexed { i, _ ->
                i != index
            }
        }
    }

    private val nameForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.NonEmpty
    )

    fun onNameChange(name: String) {
        nameState.update { nameForm(name) }
    }

    private val servingsForm = Form(
        strategy = ValidationStrategy.LazyEval,
        Rules.DigitsOnly
    )

    fun onServingsChange(servings: String) {
        servingsState.update { servingsForm(servings) }
    }

    fun onProductDelete(productId: Long) {
        viewModelScope.launch {
            foodRepository.deleteFood(FoodId.Product(productId))
        }
    }

    private val _createState = MutableStateFlow<CreateState>(CreateState.Nothing)
    val createState = _createState.asStateFlow()

    fun onCreate() {
        if (_createState.value is CreateState.CreatingRecipe) return

        val state = state.value
        val ingredientsState = state.ingredients

        val name = state.name.takeIf { it.isValid }?.value ?: return
        val servings = state.servings.takeIf { it.isValid }?.value?.toIntOrNull() ?: return

        viewModelScope.launch {
            _createState.value = CreateState.CreatingRecipe
            val id = if (recipeId != -1L) {
                recipeRepository.updateRecipe(
                    id = recipeId,
                    name = name,
                    servings = servings,
                    ingredients = ingredientsState
                )

                recipeId
            } else {
                recipeRepository.createRecipe(
                    name = name,
                    servings = servings,
                    ingredients = ingredientsState
                )
            }
            _createState.value = CreateState.Created(id)
        }
    }
}
