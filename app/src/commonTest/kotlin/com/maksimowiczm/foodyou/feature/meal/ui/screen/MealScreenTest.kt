package com.maksimowiczm.foodyou.feature.meal.ui.screen

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onNodeWithTag
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
import com.maksimowiczm.foodyou.ext.AnimatedSharedTransitionLayout
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDate
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MealScreenTest {

    @OptIn(ExperimentalTime::class, ExperimentalSharedTransitionApi::class)
    @Composable
    private fun MealScreen(
        modifier: Modifier = Modifier.Companion,
        meal: Meal = testMeal(),
        date: LocalDate = LocalDate.Companion.now(),
        foods: List<FoodWithMeasurement> = listOf(testProductWithMeasurement()),
        deletedMeasurement: Flow<MeasurementId> = emptyFlow(),
        onAddFood: () -> Unit = {},
        onBarcodeScanner: () -> Unit = {},
        onEditMeasurement: (MeasurementId) -> Unit = {},
        onDeleteEntry: (MeasurementId) -> Unit = {},
        onRestoreEntry: (MeasurementId) -> Unit = {}
    ) {
        AnimatedSharedTransitionLayout {
            MealScreen(
                screenSts = sharedTransitionScope,
                screenScope = animatedVisibilityScope,
                enterSts = sharedTransitionScope,
                enterScope = animatedVisibilityScope,
                meal = meal,
                foods = foods,
                deletedMeasurementFlow = deletedMeasurement,
                date = date,
                onAddFood = onAddFood,
                onBarcodeScanner = onBarcodeScanner,
                onEditMeasurement = onEditMeasurement,
                onDeleteEntry = onDeleteEntry,
                onRestoreEntry = onRestoreEntry,
                modifier = modifier
            )
        }
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
        onNodeWithTag(MealScreenTestTags.FOOD_ITEMS).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.FoodItem(productId)).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.SNACKBAR).assertIsNotDisplayed()
        onNodeWithTag(MealScreenTestTags.BOTTOM_SHEET).assertDoesNotExist()
    }

    @Test
    fun empty_foods_initial() = runComposeUiTest {
        setContent {
            MealScreen(
                foods = emptyList()
            )
        }

        onNodeWithTag(MealScreenTestTags.ADD_FOOD_FAB).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.BARCODE_SCANNER_FAB).assertIsDisplayed()
        onNodeWithTag(MealScreenTestTags.FOOD_ITEMS).assertIsNotDisplayed()
        onNodeWithTag(MealScreenTestTags.SNACKBAR).assertIsNotDisplayed()
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
                )
            )
        }

        onNodeWithTag(MealScreenTestTags.FoodItem(productId)).performClick()
        onNodeWithTag(MealScreenTestTags.BOTTOM_SHEET).assertIsDisplayed()
    }

    @Test
    fun show_snackbar_after_delete() = runComposeUiTest {
        val productId = FoodId.Product(1L)
        val measurementId = MeasurementId.Product(1L)

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
                deletedMeasurement = flowOf(measurementId)
            )
        }

        waitUntil {
            onNodeWithTag(MealScreenTestTags.SNACKBAR).isDisplayed()
        }
    }

    @Test
    fun show_delete_dialog_after_delete() = runComposeUiTest {
        val productId = FoodId.Product(1L)
        val measurementId = MeasurementId.Product(1L)

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
                deletedMeasurement = flowOf(measurementId)
            )
        }

        // Open the bottom sheet
        onNodeWithTag(MealScreenTestTags.FoodItem(productId)).performClick()
        waitUntil {
            onNodeWithTag(MealScreenTestTags.BOTTOM_SHEET).isDisplayed()
        }

        // Click the delete button
        onNodeWithTag(MealScreenTestTags.DELETE_ENTRY_BUTTON).performClick()

        waitUntil {
            onNodeWithTag(MealScreenTestTags.DELETE_DIALOG).isDisplayed()
        }
    }
}
