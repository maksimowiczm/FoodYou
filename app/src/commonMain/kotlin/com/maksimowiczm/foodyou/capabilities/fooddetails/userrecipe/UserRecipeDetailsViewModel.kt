package com.maksimowiczm.foodyou.capabilities.fooddetails.userrecipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.capabilities.fooddetails.FoodDetailsQuantityDelegate
import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.food.AbsoluteQuantity
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.common.domain.food.QuantityCalculator
import com.maksimowiczm.foodyou.common.domain.food.QuantityType
import com.maksimowiczm.foodyou.common.domain.food.toQuantity
import com.maksimowiczm.foodyou.common.getOrNull
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserRecipeDetailsViewModel(
    private val identity: UserRecipeIdentity,
    initialQuantity: Quantity?,
    private val userRecipeService: UserRecipeService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val eventChannel = Channel<UserRecipeDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    private val quantityDelegate = FoodDetailsQuantityDelegate(savedStateHandle, initialQuantity)

    val uiState =
        combine(
                userRecipeService.observe(identity),
                observeIsFavoriteFoodUseCase.observe(identity),
                quantityDelegate.selectedQuantityType,
                quantityDelegate.selectedQuantity,
            ) { recipe, isFavorite, selectedQuantityType, selectedQuantity ->
                if (recipe == null) {
                    return@combine UserRecipeDetailsUiState(isFavorite = isFavorite)
                }

                val servingQuantity = AbsoluteQuantity.Weight(recipe.servingWeight)
                val packageQuantity = AbsoluteQuantity.Weight(recipe.totalWeight)

                val result =
                    quantityDelegate.calculate(
                        product =
                            FoodDetailsQuantityDelegate.QuantityProvider(
                                servingQuantity,
                                packageQuantity,
                                recipe.nutritionFacts,
                                false,
                            ),
                        currentSelectedQuantity = selectedQuantity,
                        currentSelectedQuantityType = selectedQuantityType,
                    )

                val ingredientScalingFactor = run {
                    val totalWeight = recipe.totalWeight.takeIf { it.grams > 0 } ?: return@run 1.0
                    val servingWeight = recipe.servingWeight

                    val selectedAbsoluteQuantity =
                        QuantityCalculator.calculateAbsoluteQuantity(
                                suggestedQuantity = result.selectedQuantity,
                                packageQuantity = AbsoluteQuantity.Weight(totalWeight),
                                servingQuantity = AbsoluteQuantity.Weight(servingWeight),
                            )
                            .getOrNull() ?: return@run 1.0

                    when (selectedAbsoluteQuantity) {
                        is AbsoluteQuantity.Weight -> selectedAbsoluteQuantity.weight / totalWeight
                        is AbsoluteQuantity.Volume -> 1.0
                    }
                }

                UserRecipeDetailsUiState(
                    recipe = recipe,
                    isFavorite = isFavorite,
                    suggestions = result.suggestions,
                    selectedQuantity = result.selectedQuantity,
                    scaledNutritionFacts = result.scaledNutritionFacts,
                    ingredientScalingFactor = ingredientScalingFactor,
                    quantityTypes = result.quantityTypes,
                    selectedQuantityType = result.selectedQuantityType,
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = UserRecipeDetailsUiState(),
            )

    fun selectQuantity(quantity: Quantity) {
        quantityDelegate.selectQuantity(quantity)
    }

    fun selectQuantity(amount: Double?, type: QuantityType?) {
        if (amount != null && amount > 0.0 && type != null) {
            selectQuantity(type.toQuantity(amount))
        }
    }

    fun selectQuantityType(type: QuantityType) {
        quantityDelegate.selectQuantityType(type)
    }

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.Recipe(identity.id),
                isFavorite = isFavorite,
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            userRecipeService.delete(identity, DeleteStrategy.Delete)
            eventChannel.send(UserRecipeDetailsUiEvent.Deleted)
        }
    }
}
