package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UpdateFoodDiaryEntryViewModel(
    private val entryId: FoodDiaryEntryId,
    private val foodDiaryService: FoodDiaryService,
    accountService: AccountService,
) : ViewModel() {
    val entry =
        foodDiaryService
            .observe(entryId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    val profiles =
        accountService
            .observe()
            .filterNotNull()
            .map { account ->
                account.profiles.map { profile ->
                    ProfileUiState(id = profile.id, name = profile.name, avatar = profile.avatar)
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5.seconds),
                initialValue = null,
            )

    private val eventBus = Channel<FoodDiaryEntryUpdatedUiEvent>()
    val updatedEvent = eventBus.receiveAsFlow()

    fun update(
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: Instant,
    ) {
        viewModelScope.launch {
            val entry = this@UpdateFoodDiaryEntryViewModel.entry.first() ?: return@launch

            foodDiaryService.edit(
                id = entryId,
                profileIds = profiles.toSet(),
                quantity =
                    FoodSnapshotQuantityUpdateService.map(
                        quantity = quantity,
                        servingWeight = entry.snapshot.quantity.servingWeight,
                        packageWeight = entry.snapshot.quantity.packageWeight,
                    ),
                timestamp = timestamp,
            )
            eventBus.send(FoodDiaryEntryUpdatedUiEvent)
        }
    }
}
