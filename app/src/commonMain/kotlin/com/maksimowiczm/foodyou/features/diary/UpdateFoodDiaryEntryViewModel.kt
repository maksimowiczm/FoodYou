package com.maksimowiczm.foodyou.features.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maksimowiczm.foodyou.account.application.AccountService
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantityUpdateService
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.Quantity
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filterNotNull
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

    fun relink(
        profiles: List<ProfileId>,
        timestamp: Instant,
        relinkedSnapshot: MeasuredFoodSnapshot,
    ) {
        viewModelScope.launch {
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Update { current ->
                        current.copy(
                            profileIds = profiles.toSet(),
                            snapshot = relinkedSnapshot,
                            timestamp = timestamp,
                        )
                    },
            )
            eventBus.send(FoodDiaryEntryUpdatedUiEvent)
        }
    }

    fun update(
        quantity: Quantity,
        profiles: List<ProfileId>,
        timestamp: Instant,
        isTracked: Boolean,
    ) {
        viewModelScope.launch {
            foodDiaryService.handle(
                id = entryId,
                command =
                    FoodDiaryCommand.Update { current ->
                        val snapshot =
                            current.snapshot.copy(
                                quantity =
                                    FoodSnapshotQuantityUpdateService.map(
                                        quantity = quantity,
                                        servingWeight = current.snapshot.quantity.servingWeight,
                                        packageWeight = current.snapshot.quantity.packageWeight,
                                    )
                            )

                        current.copy(
                            profileIds = profiles.toSet(),
                            snapshot = if (!isTracked) snapshot.anonymize() else snapshot,
                            timestamp = timestamp,
                        )
                    },
            )
            eventBus.send(FoodDiaryEntryUpdatedUiEvent)
        }
    }
}
