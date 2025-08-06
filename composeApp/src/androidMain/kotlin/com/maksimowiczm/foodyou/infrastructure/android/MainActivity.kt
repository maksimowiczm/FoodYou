package com.maksimowiczm.foodyou.infrastructure.android

import android.os.Bundle
import androidx.compose.runtime.LaunchedEffect
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryCommand
import com.maksimowiczm.foodyou.business.fooddiary.application.command.CreateDiaryEntryError
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodProduct
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipe
import com.maksimowiczm.foodyou.business.fooddiary.domain.DiaryFoodRecipeIngredient
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.business.shared.domain.nutrients.NutritionFacts
import com.maksimowiczm.foodyou.business.shared.infrastructure.persistence.room.fooddiary.DiaryProductEntity
import com.maksimowiczm.foodyou.shared.common.date.now
import com.maksimowiczm.foodyou.shared.common.domain.infrastructure.command.CommandBus
import com.maksimowiczm.foodyou.ui.FoodYouApp
import kotlinx.datetime.LocalDate
import org.koin.compose.koinInject

class MainActivity : FoodYouAbstractActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val commandBus: CommandBus = koinInject()

            val diaryFood = DiaryFoodRecipe(
                name = "Sample Recipe",
                servings = 2,
                ingredients = listOf(
                    DiaryFoodRecipeIngredient(
                        food = DiaryFoodRecipe(
                            name = "Ingredient recipe",
                            servings = 1,
                            ingredients = listOf(
                                DiaryFoodRecipeIngredient(
                                    food = DiaryFoodProduct(
                                        name = "Nested ingredient product",
                                        nutritionFacts = NutritionFacts(),
                                        servingWeight = 100.0,
                                        totalWeight = 200.0
                                    ),
                                    measurement = Measurement.Gram(100.0)
                                )
                            )
                        ),
                        measurement = Measurement.Gram(100.0)
                    ),
                    DiaryFoodRecipeIngredient(
                        food = DiaryFoodProduct(
                            name = "Ingredient product",
                            nutritionFacts = NutritionFacts(),
                            servingWeight = 100.0,
                            totalWeight = 200.0
                        ),
                        measurement = Measurement.Gram(100.0)
                    )
                )
            )

            val command = CreateDiaryEntryCommand(
                measurement = Measurement.Gram(100.0),
                mealId = 1L,
                date = LocalDate.now(),
                food = diaryFood,
            )

            LaunchedEffect(Unit) {
                commandBus.dispatch<Long, CreateDiaryEntryError>(command)
            }

            FoodYouApp()
        }
    }
}
