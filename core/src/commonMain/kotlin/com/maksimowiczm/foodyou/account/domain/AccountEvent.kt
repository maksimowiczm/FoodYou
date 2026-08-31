package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.event.DomainEvent
import kotlin.time.Instant
import kotlin.uuid.Uuid
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.InstantComponentSerializer

@Serializable sealed class AccountEvent(override val id: Uuid = Uuid.random()) : DomainEvent

@Serializable
data class OnboardingFinishedEvent(
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant
) : AccountEvent()

@Serializable
data class ProfileAddedEvent(
    val profile: Profile,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class ProfileUpdatedEvent(
    val profile: Profile,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class ProfileRemovedEvent(
    val profileId: ProfileId,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class EnergyUnitChangedEvent(
    val unit: EnergyUnit,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class NutrientsOrderChangedEvent(
    val order: List<NutrientsOrder>,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class FavoriteFoodAddedEvent(
    val profileId: ProfileId,
    val foodId: FavoriteFoodId,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()

@Serializable
data class FavoriteFoodRemovedEvent(
    val profileId: ProfileId,
    val favoriteFoodId: FavoriteFoodId,
    @Serializable(with = InstantComponentSerializer::class) override val timestamp: Instant,
) : AccountEvent()
