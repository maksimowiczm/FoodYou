@file:MustUseReturnValues

package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.Decider
import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId

data class Account(
    val profiles: List<Profile> = emptyList(),
    val energyUnit: EnergyUnit = EnergyUnit.Kilocalories,
    val nutrientsOrder: List<NutrientsOrder> = NutrientsOrder.defaultOrder,
    val onboardingFinished: Boolean = false,
    val singleProfileMode: Boolean = false,
)

fun Account.decide(command: AccountCommand): List<AccountEvent> =
    when (command) {
        is AccountCommand.FinishOnboarding ->
            buildList {
                check(profiles.isNotEmpty()) { "Cannot finish onboarding without a profile" }
                if (!onboardingFinished) add(OnboardingFinishedEvent(timestamp = command.timestamp))
            }

        is AccountCommand.ChangeEnergyUnit ->
            buildList {
                if (energyUnit != command.unit) {
                    add(EnergyUnitChangedEvent(unit = command.unit, timestamp = command.timestamp))
                }
            }

        is AccountCommand.ChangeNutrientsOrder ->
            buildList {
                val orderSet = command.order.toSet()
                require(orderSet.size == command.order.size) {
                    "Nutrients order cannot contain duplicates"
                }
                require(orderSet == NutrientsOrder.entries.toSet()) {
                    "Nutrients order must contain all NutrientsOrder values exactly once"
                }
                if (nutrientsOrder != command.order) {
                    add(
                        NutrientsOrderChangedEvent(
                            order = command.order,
                            timestamp = command.timestamp,
                        )
                    )
                }
            }

        is AccountCommand.ChangeEnableSingleProfileMode ->
            buildList {
                if (singleProfileMode == command.enable) return@buildList

                if (command.enable)
                    check(profiles.size == 1) {
                        "Cannot enable single profile mode when there is more than one profile"
                    }

                add(
                    EnableSingleProfileModeChangedEvent(
                        enable = command.enable,
                        timestamp = command.timestamp,
                    )
                )
            }

        is AccountCommand.AddProfile ->
            buildList {
                check(profiles.none { it.id == command.profile.id }) {
                    "Profile with ID ${command.profile.id} already exists"
                }
                add(ProfileAddedEvent(profile = command.profile, timestamp = command.timestamp))
                if (profiles.size == 1 && singleProfileMode)
                    add(
                        EnableSingleProfileModeChangedEvent(
                            enable = false,
                            timestamp = command.timestamp,
                        )
                    )
            }

        is AccountCommand.UpdateProfile ->
            buildList {
                val profile = profiles.find { it.id == command.profileId }
                checkNotNull(profile) { "Profile with ID ${command.profileId} not found" }
                val updated = command.transform(profile)
                check(updated.id == command.profileId) { "Cannot change profile ID" }
                if (updated != profile) {
                    add(ProfileUpdatedEvent(profile = updated, timestamp = command.timestamp))
                }
            }

        is AccountCommand.RemoveProfile ->
            buildList {
                check(profiles.any { it.id == command.profileId }) {
                    "Profile with ID ${command.profileId} not found"
                }
                check(profiles.size > 1) { "Cannot remove the last profile" }
                add(
                    ProfileRemovedEvent(
                        profileId = command.profileId,
                        timestamp = command.timestamp,
                    )
                )
            }

        is AccountCommand.AddFavoriteFood ->
            buildList {
                val profile = profiles.find { it.id == command.profileId }
                checkNotNull(profile) { "Profile with ID ${command.profileId} not found" }
                if (command.foodId !in profile.favoriteFoods) {
                    add(
                        FavoriteFoodAddedEvent(
                            profileId = command.profileId,
                            foodId = command.foodId,
                            timestamp = command.timestamp,
                        )
                    )
                }
            }

        is AccountCommand.RemoveFavoriteFood ->
            buildList {
                val profile = profiles.find { it.id == command.profileId }
                checkNotNull(profile) { "Profile with ID ${command.profileId} not found" }
                if (command.foodId in profile.favoriteFoods) {
                    add(
                        FavoriteFoodRemovedEvent(
                            profileId = command.profileId,
                            favoriteFoodId = command.foodId,
                            timestamp = command.timestamp,
                        )
                    )
                }
            }

        is AccountCommand.RemoveFavoriteUserFood -> {
            val favoriteId = FavoriteFoodId.UserProduct(command.id.value)
            profiles
                .filter { favoriteId in it.favoriteFoods }
                .map { profile ->
                    FavoriteFoodRemovedEvent(
                        profileId = profile.id,
                        favoriteFoodId = favoriteId,
                        timestamp = command.timestamp,
                    )
                }
        }
    }

fun Account.apply(event: AccountEvent): Account =
    when (event) {
        is OnboardingFinishedEvent -> copy(onboardingFinished = true)
        is EnergyUnitChangedEvent -> copy(energyUnit = event.unit)
        is NutrientsOrderChangedEvent -> copy(nutrientsOrder = event.order)
        is EnableSingleProfileModeChangedEvent -> copy(singleProfileMode = event.enable)
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
 * Internal helper used during event replay. Unlike [decide], this does not enforce business rules —
 * it applies the event as-is, making replay tolerant of unknown or missing profile IDs that could
 * arise from out-of-order or partial event streams. Missing profiles are silently skipped rather
 * than throwing.
 */
private fun Account.applyProfileUpdate(id: ProfileId, transform: (Profile) -> Profile): Account =
    copy(profiles = profiles.map { if (it.id == id) transform(it) else it })

fun Iterable<AccountEvent>.toAccount(): Account =
    fold(Account()) { state, event -> state.apply(event) }

val accountDecider =
    Decider<AccountCommand, AccountEvent, Account>(
        decide = { command, state -> state.decide(command) },
        evolve = { state, event -> state.apply(event) },
        initialState = Account(),
    )
