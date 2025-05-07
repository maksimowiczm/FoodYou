package com.maksimowiczm.foodyou.feature.meal.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.FoodWithMeasurement
import com.maksimowiczm.foodyou.core.domain.model.Meal
import com.maksimowiczm.foodyou.core.domain.model.MeasurementId
import com.maksimowiczm.foodyou.core.domain.model.testMeal
import com.maksimowiczm.foodyou.core.domain.model.testProduct
import com.maksimowiczm.foodyou.core.domain.model.testProductWithMeasurement
import com.maksimowiczm.foodyou.core.ext.now
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import kotlinx.datetime.LocalDate
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTestApi::class)
class MealScreenTest {

    @OptIn(ExperimentalTime::class)
    @Composable
    private fun MealScreen(
        modifier: Modifier = Modifier.Companion,
        meal: Meal = testMeal(),
        date: LocalDate = LocalDate.Companion.now(),
        foods: List<FoodWithMeasurement> = listOf(testProductWithMeasurement()),
        onAddFood: () -> Unit = {},
        onBarcodeScanner: () -> Unit = {},
        onEditMeasurement: (MeasurementId) -> Unit = {},
        onDeleteEntry: (MeasurementId) -> Unit = {}
    ) {
        com.maksimowiczm.foodyou.feature.meal.ui.screen.MealScreen(
            meal = meal,
            foods = foods,
            date = date,
            onAddFood = onAddFood,
            onBarcodeScanner = onBarcodeScanner,
            onEditMeasurement = onEditMeasurement,
            onDeleteEntry = onDeleteEntry,
            modifier = modifier
        )
    }

    @Test
    fun non_empty_foods_initial() = runComposeUiTest {
        val productId = FoodId.Product(1L)

        setContent {
            MealScreen(
                foods = listOf(
                    testProductWithMeasurement(
                        product = testProduct(
                            id = productId
                        )
                    )
                )
            )
        }

        onNodeWithTag(MealScreenTestTags.ADD_FOOD_FAB).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.BARCODE_SCANNER_FAB).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.FoodItem(productId)).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.BOTTOM_SHEET).assertDoesNotExist()
    }

    @Test
    fun show_bottom_sheet_after_food_click() = runComposeUiTest {
        val productId = FoodId.Product(1L)

        setContent {
            MealScreen(
                meal = testMeal(),
                foods = listOf(
                    testProductWithMeasurement(
                        product = testProduct(
                            id = productId
                        )
                    )
                ),
                onAddFood = {},
                onBarcodeScanner = {},
                onEditMeasurement = {}
            )
        }

        onNodeWithTag(MealScreenTestTags.FoodItem(productId)).performClick()
        onNodeWithTag(MealScreenTestTags.BOTTOM_SHEET).assertIsDisplayed()
    }
}