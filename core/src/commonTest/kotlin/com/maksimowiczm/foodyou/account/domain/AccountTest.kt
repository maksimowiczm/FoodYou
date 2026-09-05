package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.domain.EnergyUnit
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.userproduct.domain.UserProductId
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid

class AccountTest {
    private val now = Instant.fromEpochMilliseconds(1000)

    private val profile1 =
        Profile(
            id = testProfileId(Uuid.parse("00000000-0000-0000-0000-000000000001")),
            name = "Profile 1",
            avatar = Profile.Avatar.Predefined(Profile.Avatar.Predefined.Variant.Person),
        )

    private val profile2 =
        Profile(
            id = testProfileId(Uuid.parse("00000000-0000-0000-0000-000000000002")),
            name = "Profile 2",
            avatar = Profile.Avatar.Predefined(Profile.Avatar.Predefined.Variant.Woman),
        )

    @Test
    fun finishOnboarding_returnsOnboardingFinishedEvent_whenNotFinished() {
        val account = Account(profiles = listOf(profile1), onboardingFinished = false)
        val events = account.decide(AccountCommand.FinishOnboarding(now))
        assertEquals(listOf(OnboardingFinishedEvent(now)), events)
    }

    @Test
    fun finishOnboarding_returnsEmptyList_whenAlreadyFinished() {
        val account = Account(profiles = listOf(profile1), onboardingFinished = true)
        val events = account.decide(AccountCommand.FinishOnboarding(now))
        assertTrue(events.isEmpty())
    }

    @Test
    fun finishOnboarding_fails_whenNoProfiles() {
        val account = Account(profiles = emptyList(), onboardingFinished = false)
        assertFailsWith<IllegalStateException> {
            account.decide(AccountCommand.FinishOnboarding(now))
        }
    }

    @Test
    fun changeEnergyUnit_returnsEnergyUnitChangedEvent_whenUnitIsDifferent() {
        val account = Account(energyUnit = EnergyUnit.Kilocalories)
        val events = account.decide(AccountCommand.ChangeEnergyUnit(EnergyUnit.Kilojoules, now))
        assertEquals(listOf(EnergyUnitChangedEvent(EnergyUnit.Kilojoules, now)), events)
    }

    @Test
    fun changeEnergyUnit_returnsEmptyList_whenUnitIsSame() {
        val account = Account(energyUnit = EnergyUnit.Kilocalories)
        val events = account.decide(AccountCommand.ChangeEnergyUnit(EnergyUnit.Kilocalories, now))
        assertTrue(events.isEmpty())
    }

    @Test
    fun changeNutrientsOrder_returnsNutrientsOrderChangedEvent_whenOrderIsDifferent() {
        val account = Account(nutrientsOrder = NutrientsOrder.defaultOrder)
        val newOrder = NutrientsOrder.defaultOrder.reversed()
        val events = account.decide(AccountCommand.ChangeNutrientsOrder(newOrder, now))
        assertEquals(listOf(NutrientsOrderChangedEvent(newOrder, now)), events)
    }

    @Test
    fun changeNutrientsOrder_fails_whenOrderHasDuplicates() {
        val account = Account()
        val invalidOrder =
            listOf(NutrientsOrder.Proteins, NutrientsOrder.Proteins) +
                (NutrientsOrder.entries - NutrientsOrder.Proteins).take(4)
        assertFailsWith<IllegalArgumentException> {
            account.decide(AccountCommand.ChangeNutrientsOrder(invalidOrder, now))
        }
    }

    @Test
    fun changeNutrientsOrder_fails_whenOrderIsMissingEntries() {
        val account = Account()
        val invalidOrder = NutrientsOrder.entries.take(1)
        assertFailsWith<IllegalArgumentException> {
            account.decide(AccountCommand.ChangeNutrientsOrder(invalidOrder, now))
        }
    }

    @Test
    fun addProfile_returnsProfileAddedEvent() {
        val account = Account(profiles = listOf(profile1))
        val events = account.decide(AccountCommand.AddProfile(profile2, now))
        assertEquals(listOf(ProfileAddedEvent(profile2, now)), events)
    }

    @Test
    fun addProfile_fails_whenProfileAlreadyExists() {
        val account = Account(profiles = listOf(profile1))
        assertFailsWith<IllegalStateException> {
            account.decide(AccountCommand.AddProfile(profile1, now))
        }
    }

    @Test
    fun updateProfile_returnsProfileUpdatedEvent() {
        val account = Account(profiles = listOf(profile1))
        val transform: (Profile) -> Profile = { it.copy(name = "Updated Name") }
        val events = account.decide(AccountCommand.UpdateProfile(profile1.id, now, transform))
        val updatedProfile = profile1.copy(name = "Updated Name")
        assertEquals(listOf(ProfileUpdatedEvent(updatedProfile, now)), events)
    }

    @Test
    fun updateProfile_fails_whenProfileDoesNotExist() {
        val account = Account(profiles = listOf(profile1))
        assertFailsWith<IllegalStateException> {
            account.decide(AccountCommand.UpdateProfile(profile2.id, now) { it })
        }
    }

    @Test
    fun removeProfile_returnsProfileRemovedEvent() {
        val account = Account(profiles = listOf(profile1, profile2))
        val events = account.decide(AccountCommand.RemoveProfile(profile2.id, now))
        assertEquals(listOf(ProfileRemovedEvent(profile2.id, now)), events)
    }

    @Test
    fun removeProfile_fails_whenProfileDoesNotExist() {
        val account = Account(profiles = listOf(profile1, profile2))
        assertFailsWith<IllegalStateException> {
            account.decide(AccountCommand.RemoveProfile(ProfileId(Uuid.random()), now))
        }
    }

    @Test
    fun removeProfile_fails_whenItsTheLastProfile() {
        val account = Account(profiles = listOf(profile1))
        assertFailsWith<IllegalStateException> {
            account.decide(AccountCommand.RemoveProfile(profile1.id, now))
        }
    }

    @Test
    fun addFavoriteFood_returnsFavoriteFoodAddedEvent() {
        val account = Account(profiles = listOf(profile1))
        val food = FavoriteFoodId.FoodDataCentral(123)
        val events = account.decide(AccountCommand.AddFavoriteFood(profile1.id, food, now))
        assertEquals(listOf(FavoriteFoodAddedEvent(profile1.id, food, now)), events)
    }

    @Test
    fun addFavoriteFood_isIdempotent() {
        val food = FavoriteFoodId.FoodDataCentral(123)
        val account = Account(profiles = listOf(profile1.copy(favoriteFoods = setOf(food))))
        val events = account.decide(AccountCommand.AddFavoriteFood(profile1.id, food, now))
        assertTrue(events.isEmpty())
    }

    @Test
    fun removeFavoriteFood_returnsFavoriteFoodRemovedEvent() {
        val food = FavoriteFoodId.FoodDataCentral(123)
        val account = Account(profiles = listOf(profile1.copy(favoriteFoods = setOf(food))))
        val events = account.decide(AccountCommand.RemoveFavoriteFood(profile1.id, food, now))
        assertEquals(listOf(FavoriteFoodRemovedEvent(profile1.id, food, now)), events)
    }

    @Test
    fun removeFavoriteFood_isIdempotent() {
        val food = FavoriteFoodId.FoodDataCentral(123)
        val account = Account(profiles = listOf(profile1))
        val events = account.decide(AccountCommand.RemoveFavoriteFood(profile1.id, food, now))
        assertTrue(events.isEmpty())
    }

    @Test
    fun removeFavoriteUserFood_returnsEventsForAllProfilesContainingTheFood() {
        val productId = Uuid.random()
        val food = FavoriteFoodId.UserProduct(productId)
        val account =
            Account(
                profiles =
                    listOf(
                        profile1.copy(favoriteFoods = setOf(food)),
                        profile2.copy(favoriteFoods = setOf(food)),
                    )
            )

        val events =
            account.decide(AccountCommand.RemoveFavoriteUserFood(UserProductId(productId), now))
        assertEquals(2, events.size)
        assertTrue(events.all { it is FavoriteFoodRemovedEvent && it.favoriteFoodId == food })
        assertTrue(events.any { (it as FavoriteFoodRemovedEvent).profileId == profile1.id })
        assertTrue(events.any { (it as FavoriteFoodRemovedEvent).profileId == profile2.id })
    }

    @Test
    fun apply_eventsUpdateAccountState() {
        var account = Account()

        account = account.apply(ProfileAddedEvent(profile1, now))

        account = account.apply(OnboardingFinishedEvent(now))
        assertTrue(account.onboardingFinished)

        account = account.apply(EnergyUnitChangedEvent(EnergyUnit.Kilojoules, now))
        assertEquals(EnergyUnit.Kilojoules, account.energyUnit)

        val newOrder = NutrientsOrder.defaultOrder.reversed()
        account = account.apply(NutrientsOrderChangedEvent(newOrder, now))
        assertEquals(newOrder, account.nutrientsOrder)

        val updatedProfile = profile1.copy(name = "New Name")
        account = account.apply(ProfileUpdatedEvent(updatedProfile, now))
        assertEquals("New Name", account.profiles.first().name)

        account = account.apply(ProfileAddedEvent(profile2, now))
        account = account.apply(ProfileRemovedEvent(profile1.id, now))
        assertEquals(listOf(profile2), account.profiles)

        val food = FavoriteFoodId.FoodDataCentral(1)
        account = account.apply(FavoriteFoodAddedEvent(profile2.id, food, now))
        assertTrue(food in account.profiles.first().favoriteFoods)

        account = account.apply(FavoriteFoodRemovedEvent(profile2.id, food, now))
        assertTrue(food !in account.profiles.first().favoriteFoods)
    }

    @Test
    fun toAccount_reconstructsAccountFromEvents() {
        val events =
            listOf(
                ProfileAddedEvent(profile1, now),
                EnergyUnitChangedEvent(EnergyUnit.Kilojoules, now),
                OnboardingFinishedEvent(now),
            )

        val account = events.toAccount()
        assertEquals(listOf(profile1), account.profiles)
        assertEquals(EnergyUnit.Kilojoules, account.energyUnit)
        assertTrue(account.onboardingFinished)
    }

    @Test
    fun init_fails_whenDuplicateProfileIds() {
        assertFailsWith<IllegalArgumentException> { Account(profiles = listOf(profile1, profile1)) }
    }

    @Test
    fun init_fails_whenOnboardingFinishedWithoutProfiles() {
        assertFailsWith<IllegalArgumentException> {
            Account(profiles = emptyList(), onboardingFinished = true)
        }
    }

    @Test
    fun init_fails_whenNutrientsOrderHasDuplicates() {
        val invalidOrder =
            listOf(NutrientsOrder.Proteins, NutrientsOrder.Proteins) +
                (NutrientsOrder.entries - NutrientsOrder.Proteins).take(4)
        assertFailsWith<IllegalArgumentException> { Account(nutrientsOrder = invalidOrder) }
    }

    @Test
    fun init_fails_whenNutrientsOrderIsMissingEntries() {
        val invalidOrder = NutrientsOrder.entries.take(1)
        assertFailsWith<IllegalArgumentException> { Account(nutrientsOrder = invalidOrder) }
    }
}
