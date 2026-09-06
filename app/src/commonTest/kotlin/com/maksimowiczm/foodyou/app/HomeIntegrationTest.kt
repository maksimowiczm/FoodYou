package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.AnonymousFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.features.home.integration.HomeDao
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryCommand
import com.maksimowiczm.foodyou.fooddiary.domain.FoodDiaryEntryId
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.Koin

// TODO sometimes tests will suspend indefinitely
@Ignore
class HomeIntegrationTest {
    private val profileId = ProfileId()
    private val mealId = MealId()

    @Test
    fun creating_diary_entry_inserts_it_into_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val dateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                val entries =
                    homeDao.observeEntries(profileId.value, dateTime.date).first { it.isNotEmpty() }

                val entry = entries.single { it.entryId == entryId.id }
                assertEquals(composition, entry.snapshot)
                assertEquals(mealId.value, entry.mealId)
                assertEquals(dateTime.date, entry.date)
                assertEquals(dateTime.time.hour, entry.time.hour)
                assertEquals(dateTime.time.minute, entry.time.minute)
            }
        }

    @Test
    fun updating_diary_entry_updates_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                val updatedQuantity =
                    FoodSnapshotQuantity.Weight(
                        absoluteWeight = 200.grams,
                        servingWeight = null,
                        packageWeight = null,
                    )
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Update(timestamp = Clock.System.now()) { current ->
                            current.copy(
                                profileIds = setOf(profileId),
                                snapshot = current.snapshot.withNewQuantity(updatedQuantity),
                                timestamp = timestamp,
                            )
                        },
                )

                val entries =
                    homeDao.observeEntries(profileId.value, date).first {
                        it.any { e ->
                            e.entryId == entryId.id && e.snapshot.quantity == updatedQuantity
                        }
                    }

                val entry = entries.single { it.entryId == entryId.id }
                assertEquals(updatedQuantity, entry.snapshot.quantity)
            }
        }

    @Test
    fun deleting_diary_entry_removes_it_from_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                foodDiaryService.handle(
                    id = entryId,
                    command = FoodDiaryCommand.Delete(timestamp = Clock.System.now()),
                )

                val entries = homeDao.observeEntries(profileId.value, date).first { it.isEmpty() }

                // A little bit useless assert, but there should be at least one assert here
                assertEquals(true, entries.isEmpty())
            }
        }

    @Test
    fun anonymizing_diary_entry_updates_composition_in_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                foodDiaryService.handle(
                    id = entryId,
                    command = FoodDiaryCommand.Anonymize(timestamp = Clock.System.now()),
                )

                val entries =
                    homeDao.observeEntries(profileId.value, date).first {
                        it.any { e ->
                            e.entryId == entryId.id && e.snapshot.snapshot is AnonymousFoodSnapshot
                        }
                    }
                val entry = entries.first { it.entryId == entryId.id }
                assertEquals(true, entry.snapshot.snapshot is AnonymousFoodSnapshot)
            }
        }

    @Test
    fun unlinking_diary_entry_from_meal_updates_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                foodDiaryService.unlinkEntriesFromMeal(mealId)

                val entries =
                    homeDao.observeEntries(profileId.value, date).first {
                        it.any { e -> e.entryId == entryId.id && e.mealId == null }
                    }
                val entry = entries.first { it.entryId == entryId.id }
                assertNull(entry.mealId)
            }
        }

    @Test
    fun creating_diary_entry_with_multiple_profiles_inserts_it_for_each_profile() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val otherProfileId = ProfileId()
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId, otherProfileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date

                val entries1 =
                    homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }
                val entry1 = entries1.single { it.entryId == entryId.id }
                assertEquals(composition, entry1.snapshot)

                val entries2 =
                    homeDao.observeEntries(otherProfileId.value, date).first { it.isNotEmpty() }
                val entry2 = entries2.single { it.entryId == entryId.id }
                assertEquals(composition, entry2.snapshot)
            }
        }

    @Test
    fun updating_diary_entry_profiles_updates_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val otherProfileId = ProfileId()
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId = FoodDiaryEntryId()
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Create(
                            id = entryId,
                            profileIds = setOf(profileId),
                            snapshot = composition,
                            mealId = mealId,
                            entryTimestamp = timestamp,
                            timestamp = Clock.System.now(),
                        ),
                )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                // Add another profile and remove the first one
                foodDiaryService.handle(
                    id = entryId,
                    command =
                        FoodDiaryCommand.Update(timestamp = Clock.System.now()) { current ->
                            current.copy(
                                profileIds = setOf(otherProfileId),
                                snapshot = current.snapshot.withNewQuantity(composition.quantity),
                                timestamp = timestamp,
                            )
                        },
                )

                // Should be removed from first profile
                val entries1 = homeDao.observeEntries(profileId.value, date).first { it.isEmpty() }
                assertEquals(0, entries1.size)

                // Should be added to second profile
                val entries2 =
                    homeDao.observeEntries(otherProfileId.value, date).first { it.isNotEmpty() }
                val entry2 = entries2.single { it.entryId == entryId.id }
                assertEquals(composition, entry2.snapshot)
            }
        }

    private fun createComposition() =
        MeasuredFoodSnapshot(
            snapshot =
                LeafFoodSnapshot(
                    id = FoodSnapshotId.UserProduct(Uuid.random()),
                    name = FoodName(english = "Test Product", fallback = "Test Product"),
                    brand = null,
                    image = null,
                    nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
                ),
            quantity =
                FoodSnapshotQuantity.Weight(
                    absoluteWeight = 100.grams,
                    servingWeight = null,
                    packageWeight = null,
                ),
        )

    private val Koin.foodDiaryService
        get() = get<FoodDiaryService>()

    private val Koin.homeDao
        get() = get<HomeDao>()
}
