package com.maksimowiczm.foodyou.app.ui.food.details.userrecipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.application.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.application.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.userrecipe.application.UserRecipeService
import com.maksimowiczm.foodyou.userrecipe.domain.UserRecipeIdentity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserRecipeDetailsViewModel(
    private val identity: UserRecipeIdentity,
    private val userRecipeService: UserRecipeService,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
) : ViewModel() {
    private val eventChannel = Channel<UserRecipeDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    val isFavorite =
        observeIsFavoriteFoodUseCase
            .observe(identity)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val userRecipe =
        userRecipeService
            .observe(identity)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

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
            userRecipeService.delete(identity)
            eventChannel.send(UserRecipeDetailsUiEvent.Deleted)
        }
    }
}
