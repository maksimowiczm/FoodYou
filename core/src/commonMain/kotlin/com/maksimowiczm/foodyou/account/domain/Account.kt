@file:MustUseReturnValues

package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlin.time.Clock

data class Account(
    val profiles: List<Profile> = emptyList(),
    val energyUnit: EnergyUnit = EnergyUnit.Kilocalories,
    val nutrientsOrder: List<NutrientsOrder> = NutrientsOrder.defaultOrder,
    val onboardingFinished: Boolean = false,
) {
    init {
        require(profiles.distinctBy { it.id }.size == profiles.size) {
            "Account cannot have duplicate profile IDs"
        }
        require(!onboardingFinished || profiles.isNotEmpty()) {
            "Account cannot have onboarding finished without any profiles"
        }
        val nutrientsSet = nutrientsOrder.toSet()
        require(nutrientsSet.size == nutrientsOrder.size) {
            "Nutrients order cannot contain duplicates"
        }
        require(nutrientsSet == NutrientsOrder.entries.toSet()) {
            "Nutrients order must contain all NutrientsOrder values exactly once"
        }
    }
}

fun Account.finishOnboarding(clock: Clock = Clock.System) =
    buildList<AccountEvent> {
        check(profiles.isNotEmpty()) { "Cannot finish onboarding without a profile" }
        if (!onboardingFinished) add(OnboardingFinishedEvent(timestamp = clock.now()))
    }

fun Account.changeEnergyUnit(unit: EnergyUnit, clock: Clock = Clock.System) =
    buildList<AccountEvent> {
        if (energyUnit != unit) add(EnergyUnitChangedEvent(unit = unit, timestamp = clock.now()))
    }

fun Account.changeNutrientsOrder(order: List<NutrientsOrder>, clock: Clock = Clock.System) =
    buildList<AccountEvent> {
        val orderSet = order.toSet()
        require(orderSet.size == order.size) { "Nutrients order cannot contain duplicates" }
        require(orderSet == NutrientsOrder.entries.toSet()) {
            "Nutrients order must contain all NutrientsOrder values exactly once"
        }
        if (nutrientsOrder != order)
            add(NutrientsOrderChangedEvent(order = order, timestamp = clock.now()))
    }

fun Account.addProfile(profile: Profile, clock: Clock = Clock.System) =
    buildList<AccountEvent> {
        check(profiles.none { it.id == profile.id }) {
            "Profile with ID ${profile.id} already exists"
        }
        add(ProfileAddedEvent(profile = profile, timestamp = clock.now()))
    }

fun Account.updateProfile(
    profileId: ProfileId,
    clock: Clock = Clock.System,
    transform: (Profile) -> Profile,
) =
    buildList<AccountEvent> {
        val profile = profiles.find { it.id == profileId }
        checkNotNull(profile) { "Profile with ID $profileId not found" }
        val updated = transform(profile)
        if (updated != profile) {
            add(ProfileUpdatedEvent(profile = updated, timestamp = clock.now()))
        }
    }

fun Account.removeProfile(id: ProfileId, clock: Clock = Clock.System) =
    buildList<AccountEvent> {
        check(profiles.any { it.id == id }) { "Profile with ID $id not found" }
        check(profiles.size > 1) { "Cannot remove the last profile" }
        add(ProfileRemovedEvent(profileId = id, timestamp = clock.now()))
    }

fun Account.addFavoriteFood(
    profileId: ProfileId,
    foodId: FavoriteFoodId,
    clock: Clock = Clock.System,
) =
    buildList<AccountEvent> {
        val profile = profiles.find { it.id == profileId }
        checkNotNull(profile) { "Profile with ID $profileId not found" }
        if (foodId in profile.favoriteFoods) return@buildList
        add(
            FavoriteFoodAddedEvent(
                profileId = profileId,
                foodId = foodId,
                timestamp = clock.now(),
            )
        )
    }

fun Account.removeFavoriteFood(
    profileId: ProfileId,
    foodId: FavoriteFoodId,
    clock: Clock = Clock.System,
) =
    buildList<AccountEvent> {
        val profile = profiles.find { it.id == profileId }
        checkNotNull(profile) { "Profile with ID $profileId not found" }
        if (foodId !in profile.favoriteFoods) return@buildList
        add(
            FavoriteFoodRemovedEvent(
                profileId = profileId,
                favoriteFoodId = foodId,
                timestamp = clock.now(),
            )
        )
    }

fun Account.removeFavoriteUserFood(
    id: UserProductId,
    clock: Clock = Clock.System,
): List<AccountEvent> {
    val id = FavoriteFoodId.UserProduct(id.value)
    return profiles
        .filter { id in it.favoriteFoods }
        .map { profile ->
            FavoriteFoodRemovedEvent(
                profileId = profile.id,
                favoriteFoodId = id,
                timestamp = clock.now(),
            )
        }
}

fun Account.apply(event: AccountEvent): Account =
    when (event) {
        is OnboardingFinishedEvent -> copy(onboardingFinished = true)
        is EnergyUnitChangedEvent -> copy(energyUnit = event.unit)
        is NutrientsOrderChangedEvent -> copy(nutrientsOrder = event.order)
        is ProfileAddedEvent -> copy(profiles = profiles + event.profile)
        is ProfileUpdatedEvent -> applyProfileUpdate(event.profile.id) { event.profile }
        is ProfileRemovedEvent -> copy(profiles = profiles.filterNot { it.id == event.profileId })
        is FavoriteFoodAddedEvent ->
            applyProfileUpdate(event.profileId) { profile ->
                profile.copy(favoriteFoods = profile.favoriteFoods + event.foodId)
            }

        is FavoriteFoodRemovedEvent ->
            applyProfileUpdate(event.profileId) { profile ->
                profile.copy(favoriteFoods = profile.favoriteFoods - event.favoriteFoodId)
            }
    }

/**
 * Internal helper used during event replay. Unlike [updateProfile], this does not enforce business
 * rules — it applies the event as-is, making replay tolerant of unknown or missing profile IDs that
 * could arise from out-of-order or partial event streams. Missing profiles are silently skipped
 * rather than throwing.
 */
private fun Account.applyProfileUpdate(id: ProfileId, transform: (Profile) -> Profile): Account =
    copy(profiles = profiles.map { if (it.id == id) transform(it) else it })

fun Iterable<AccountEvent>.toAccount(): Account =
    fold(Account()) { state, event -> state.apply(event) }
