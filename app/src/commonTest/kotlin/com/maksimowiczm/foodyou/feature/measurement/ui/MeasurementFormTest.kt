package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.domain.model.FoodId
import com.maksimowiczm.foodyou.core.domain.model.PortionWeight
import com.maksimowiczm.foodyou.core.domain.model.Product
import com.maksimowiczm.foodyou.core.domain.model.testNutritionFacts
import com.maksimowiczm.foodyou.ext.onNodeWithTag
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementFormTestKeys
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.rememberMeasurementFormState
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MeasurementFormTest {
    private fun createProduct(
        packageWeight: PortionWeight.Package? = PortionWeight.Package(100f),
        servingWeight: PortionWeight.Serving? = PortionWeight.Serving(50f)
    ) = Product(
        id = FoodId.Product(1),
        name = "Test Product",
        brand = null,
        nutritionFacts = testNutritionFacts(),
        barcode = null,
        packageWeight = packageWeight,
        servingWeight = servingWeight
    )

    @Test
    fun measurementForm_displaysCorrectValuesAll() = runComposeUiTest {
        setContent {
            MeasurementForm(
                state = rememberMeasurementFormState(createProduct()),
                onMeasurement = {}
            )
        }

        onNodeWithTag(MeasurementFormTestKeys.Package(1f)).assertIsDisplayed()
        onNodeWithTag(MeasurementFormTestKeys.Serving(1f)).assertIsDisplayed()
        onNodeWithTag(MeasurementFormTestKeys.Gram(100f)).assertIsDisplayed()
    }

    @Test
    fun measurementForm_displaysCorrectValuesWithoutPackage() = runComposeUiTest {
        setContent {
            MeasurementForm(
                state = rememberMeasurementFormState(createProduct(packageWeight = null)),
                onMeasurement = {}
            )
        }

        onNodeWithTag(MeasurementFormTestKeys.Package(1f)).assertDoesNotExist()
        onNodeWithTag(MeasurementFormTestKeys.Serving(1f)).assertIsDisplayed()
        onNodeWithTag(MeasurementFormTestKeys.Gram(100f)).assertIsDisplayed()
    }

    @Test
    fun measurementForm_displaysCorrectValuesWithoutServing() = runComposeUiTest {
        setContent {
            MeasurementForm(
                state = rememberMeasurementFormState(createProduct(servingWeight = null)),
                onMeasurement = {}
            )
        }

        onNodeWithTag(MeasurementFormTestKeys.Package(1f)).assertIsDisplayed()
        onNodeWithTag(MeasurementFormTestKeys.Serving(1f)).assertDoesNotExist()
        onNodeWithTag(MeasurementFormTestKeys.Gram(100f)).assertIsDisplayed()
    }

    @Test
    fun measurementForm_displaysCorrectValuesWithoutPackageAndServing() = runComposeUiTest {
        setContent {
            MeasurementForm(
                state = rememberMeasurementFormState(
                    createProduct(
                        packageWeight = null,
                        servingWeight = null
                    )
                ),
                onMeasurement = {}
            )
        }

        onNodeWithTag(MeasurementFormTestKeys.Package(1f)).assertDoesNotExist()
        onNodeWithTag(MeasurementFormTestKeys.Serving(1f)).assertDoesNotExist()
        onNodeWithTag(MeasurementFormTestKeys.Gram(100f)).assertIsDisplayed()
    }
}
