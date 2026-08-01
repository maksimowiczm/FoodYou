package com.maksimowiczm.foodyou.app

import com.maksimowiczm.foodyou.common.domain.food.FoodComponentComponentQuantity
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponent
import com.maksimowiczm.foodyou.common.domain.food.FoodCompositionComponentIdentity
import com.maksimowiczm.foodyou.common.domain.food.FoodName
import com.maksimowiczm.foodyou.common.domain.food.NutritionFacts
import com.maksimowiczm.foodyou.common.domain.grams
import com.maksimowiczm.foodyou.fooddiary.application.FoodDiaryService
import com.maksimowiczm.foodyou.mealplan.application.MealPlanService
import com.maksimowiczm.foodyou.mealplan.domain.Meal
import com.maksimowiczm.foodyou.mealplan.domain.MealIdentity
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
            // 1. Setup a meal plan with one meal
            val mealId = MealIdentity(Uuid.random())
            val meal = Meal(mealId, "Breakfast", Meal.TimeWindow.AllDay)
            mealPlanService.transact { it.update(listOf(meal)) }

            // 2. Create a diary entry associated with this meal
            val composition =
                FoodCompositionComponent.Simple(
                    identity = FoodCompositionComponentIdentity.OpenFoodFacts("123"),
                    name = FoodName(fallback = "Apple"),
                    image = null,
                    nutritionFacts = NutritionFacts(),
                    quantity =
                        FoodComponentComponentQuantity.Weight(
                            absoluteWeight = 100.grams,
                            servingWeight = null,
                            packageWeight = null,
                        ),
                )
            val entryId = foodDiaryService.create(composition, mealId, Clock.System.now())

            // 3. Verify association
            val entry = foodDiaryService.observe(entryId).filterNotNull().first()
            assertEquals(mealId, entry.mealIdentity)

            // 4. Remove the meal from the plan
            mealPlanService.transact { it.update(emptyList()) }

            // 5. Verify the diary entry is unlinked (mealIdentity becomes null)
            val updatedEntry =
                foodDiaryService.observe(entryId).filterNotNull().first {
                    it.mealIdentity == null
                }
            assertEquals(null, updatedEntry.mealIdentity)
        }
    }

    private val Koin.mealPlanService: MealPlanService
        get() = get()

    private val Koin.foodDiaryService: FoodDiaryService
        get() = get()
}
