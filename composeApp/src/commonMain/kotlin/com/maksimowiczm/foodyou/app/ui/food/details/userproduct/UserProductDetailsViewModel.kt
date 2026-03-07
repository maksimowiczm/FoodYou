package com.maksimowiczm.foodyou.app.ui.food.details.userproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.domain.FavoriteFoodIdentity
import com.maksimowiczm.foodyou.app.ui.food.details.ObserveIsFavoriteFoodUseCase
import com.maksimowiczm.foodyou.app.ui.food.details.SetFavoriteFoodUseCase
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductIdentity
import com.maksimowiczm.foodyou.userfood.domain.product.UserProductRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class UserProductDetailsViewModel(
    private val identity: UserProductIdentity,
    private val userProductRepository: UserProductRepository,
    observeIsFavoriteFoodUseCase: ObserveIsFavoriteFoodUseCase,
    private val setFavoriteFoodUseCase: SetFavoriteFoodUseCase,
) : ViewModel() {
    private val eventChannel = Channel<UserProductDetailsUiEvent>()
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
        userProductRepository
            .observe(identity)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(2_000),
                initialValue = null,
            )

    fun setFavorite(isFavorite: Boolean) {
        viewModelScope.launch {
            setFavoriteFoodUseCase.setFavoriteFood(
                identity = FavoriteFoodIdentity.UserProduct(identity.id),
                isFavorite = isFavorite,
            )
        }
    }

    fun delete() {
        viewModelScope.launch {
            userProductRepository.delete(identity)
            eventChannel.send(UserProductDetailsUiEvent.Deleted)
        }
    }
}
