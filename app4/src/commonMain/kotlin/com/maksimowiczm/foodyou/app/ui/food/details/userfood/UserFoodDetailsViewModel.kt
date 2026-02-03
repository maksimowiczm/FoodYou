package com.maksimowiczm.foodyou.app.ui.food.details.userfood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.details.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.userfood.domain.UserFoodProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.UserFoodRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserFoodDetailsViewModel(
    private val identity: UserFoodProductIdentity,
    private val userFoodRepository: UserFoodRepository,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
) : ViewModel() {
    private val eventChannel = Channel<UserFoodDetailsUiEvent>()
    val uiEvents = eventChannel.receiveAsFlow()

    val isFavorite =
        observeIsFavoriteFoodUseCase
            .observe(identity)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    val userFood =
        userFoodRepository
            .observe(identity)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.UserFoodProduct(identity.id),
                isFavorite = isFavorite,
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            userFoodRepository.delete(identity)
            eventChannel.send(UserFoodDetailsUiEvent.Deleted)
        }
    }
}
