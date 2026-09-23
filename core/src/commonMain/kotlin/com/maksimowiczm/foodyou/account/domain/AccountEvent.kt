package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlinx.serialization.Serializable

@Serializable sealed interface AccountEvent : DomainEvent

@Serializable data object OnboardingFinishedEvent : AccountEvent

@Serializable data class ProfileAddedEvent(val profile: Profile) : AccountEvent

@Serializable data class ProfileUpdatedEvent(val profile: Profile) : AccountEvent

@Serializable data class ProfileRemovedEvent(val profileId: ProfileId) : AccountEvent

@Serializable data class EnableSingleProfileModeChangedEvent(val enable: Boolean) : AccountEvent

@Serializable
data class FavoriteFoodAddedEvent(
    val profileId: ProfileId,
    val foodId: FavoriteFoodId,
) : AccountEvent

@Serializable
data class FavoriteFoodRemovedEvent(
    val profileId: ProfileId,
    val favoriteFoodId: FavoriteFoodId,
) : AccountEvent
