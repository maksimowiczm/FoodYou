package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.ProfileId
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotId
import com.maksimowiczm.foodyou.common.domain.food.FoodSnapshotQuantity
import com.maksimowiczm.foodyou.common.domain.food.LeafFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.MeasuredFoodSnapshot
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealId
import com.maksimowiczm.foodyou.mealplan.domain.update
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.uuid.Uuid
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.core.Koin

class MealPlanUnlinkingIntegrationTest {

    @Test
    fun `deleting meal unlinks diary entries`() = runTest {
        runKoin {
            val profileId = ProfileId(Uuid.random())
            // 1. Setup a meal plan with two meals
            val mealId = MealId(Uuid.random())
            val meal = Meal(mealId, "Breakfast", Meal.TimeWindow.AllDay)
            val otherMeal = Meal(MealId(Uuid.random()), "Lunch", Meal.TimeWindow.AllDay)
            mealPlanService.transact { it.update(listOf(meal, otherMeal)) }

            // 2. Create a diary entry associated with "Breakfast"
            val composition =
                MeasuredFoodSnapshot(
                    snapshot =
                        LeafFoodSnapshot(
                            id = FoodSnapshotId.OpenFoodFacts("123"),
                            name = FoodName(fallback = "Apple"),
                            brand = null,
                            image = null,
                            nutritionFacts = NutritionFacts(),
                        ),
                    quantity =
                        FoodSnapshotQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId =
                foodDiaryService.create(setOf(profileId), composition, mealId, Clock.System.now())

            // 3. Verify association
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(mealId, entry.mealId)

            // 4. Remove "Breakfast" from the plan, keeping "Lunch"
            mealPlanService.transact { it.update(listOf(otherMeal)) }

            // 5. Verify the diary entry is unlinked (mealId becomes null)
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.mealId == null
                }
            assertEquals(null, updatedEntry.mealId)
        }
    }

    private val Koin.mealPlanService: MealPlanService
        get() = get()

    private val Koin.foodDiaryService: FoodDiaryService
        get() = get()
}
