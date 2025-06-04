package com.maksimowiczm.foodyou.feature.measurement.ui

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.runComposeUiTest
import com.maksimowiczm.foodyou.core.model.testing.testProduct
import com.maksimowiczm.foodyou.feature.measurement.onNodeWithTag
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementForm
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.MeasurementFormTestKeys
import com.maksimowiczm.foodyou.feature.measurement.ui.basic.rememberMeasurementFormState
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class MeasurementFormTest {

    @Test
    fun measurementForm_displaysCorrectValuesAll() = runComposeUiTest {
        setContent {
            MeasurementForm(
                state = rememberMeasurementFormState(testProduct()),
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
                state = rememberMeasurementFormState(testProduct(totalWeight = null)),
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
                state = rememberMeasurementFormState(testProduct(servingWeight = null)),
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
                    testProduct(
                        totalWeight = null,
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
