package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.DeleteStrategy
import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutrientValue
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.features.home.integration.HomeDao
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
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

class HomeIntegrationTest {
    private val profileId = ProfileId()
    private val mealId = MealIdentity()

    @Test
    fun creating_diary_entry_inserts_it_into_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId =
                    foodDiaryService.create(setOf(profileId), composition, mealId, timestamp)

                val dateTime = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                val entries =
                    homeDao.observeEntries(profileId.value, dateTime.date).first { it.isNotEmpty() }

                val entry = entries.single { it.entryId == entryId.id }
                assertEquals(composition, entry.composition)
                assertEquals(mealId.id, entry.mealId)
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
                val entryId =
                    foodDiaryService.create(setOf(profileId), composition, mealId, timestamp)

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                val updatedComposition =
                    composition.copy(name = FoodName(english = "Updated", fallback = "Updated"))
                foodDiaryService.edit(entryId, updatedComposition, timestamp)

                val entries =
                    homeDao.observeEntries(profileId.value, date).first {
                        it.any { e ->
                            e.entryId == entryId.id && e.composition.name.english == "Updated"
                        }
                    }

                val entry = entries.single { it.entryId == entryId.id }
                assertEquals(updatedComposition, entry.composition)
            }
        }

    @Test
    fun deleting_diary_entry_removes_it_from_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId =
                    foodDiaryService.create(setOf(profileId), composition, mealId, timestamp)

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                foodDiaryService.delete(entryId, DeleteStrategy.Delete)

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
                val entryId =
                    foodDiaryService.create(setOf(profileId), composition, mealId, timestamp)

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date
                homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }

                foodDiaryService.delete(entryId, DeleteStrategy.Unlink)

                val entries =
                    homeDao.observeEntries(profileId.value, date).first {
                        it.any { e ->
                            e.entryId == entryId.id &&
                                e.composition is FoodCompositionComponent.Anonymous
                        }
                    }
                val entry = entries.first { it.entryId == entryId.id }
                assertEquals(true, entry.composition is FoodCompositionComponent.Anonymous)
            }
        }

    @Test
    fun unlinking_diary_entry_from_meal_updates_home_projection() =
        runTest(timeout = 5.seconds) {
            runKoin {
                val composition = createComposition()
                val timestamp = Clock.System.now()
                val entryId =
                    foodDiaryService.create(setOf(profileId), composition, mealId, timestamp)

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
                val entryId =
                    foodDiaryService.create(
                        setOf(profileId, otherProfileId),
                        composition,
                        mealId,
                        timestamp,
                    )

                val date = timestamp.toLocalDateTime(TimeZone.currentSystemDefault()).date

                val entries1 =
                    homeDao.observeEntries(profileId.value, date).first { it.isNotEmpty() }
                val entry1 = entries1.single { it.entryId == entryId.id }
                assertEquals(composition, entry1.composition)

                val entries2 =
                    homeDao.observeEntries(otherProfileId.value, date).first { it.isNotEmpty() }
                val entry2 = entries2.single { it.entryId == entryId.id }
                assertEquals(composition, entry2.composition)
            }
        }

    private fun createComposition() =
        FoodCompositionComponent.Simple(
            identity = FoodCompositionComponentIdentity.UserProduct(Uuid.random()),
            name = FoodName(english = "Test Product", fallback = "Test Product"),
            image = null,
            nutritionFacts = NutritionFacts(proteins = NutrientValue.Complete(10.grams)),
            quantity =
                FoodComponentComponentQuantity.Weight(
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
