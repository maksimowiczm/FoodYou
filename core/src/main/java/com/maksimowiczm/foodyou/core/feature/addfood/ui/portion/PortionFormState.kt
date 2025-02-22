package com.maksimowiczm.foodyou.core.feature.addfood.ui.portion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.QuantitySuggestion
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.core.feature.product.data.model.Product
import com.maksimowiczm.foodyou.core.ui.form.FormFieldWithTextFieldValue
import com.maksimowiczm.foodyou.core.ui.form.between
import com.maksimowiczm.foodyou.core.ui.form.floatParser
import com.maksimowiczm.foodyou.core.ui.form.positive
import com.maksimowiczm.foodyou.core.ui.form.rememberFormFieldWithTextFieldValue
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

@Composable
fun rememberPortionFormState(
    suggestion: QuantitySuggestion,
    initialMeasurement: WeightMeasurement = WeightMeasurement.WeightUnit(100f)
): PortionFormState {
    val packageInput = rememberFormFieldWithTextFieldValue(
        initialValue = suggestion.quantitySuggestions[WeightMeasurementEnum.Package] ?: 1f,
        validator = {
            between(
                min = 0f,
                max = 100f,
                onError = { MyError.Invalid }
            ) {
                positive(
                    onError = { MyError.Invalid }
                )
            }
        },
        parser = floatParser(
            onEmpty = { MyError.Empty },
            onNan = { MyError.Invalid }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    )

    val servingInput = rememberFormFieldWithTextFieldValue(
        initialValue = suggestion.quantitySuggestions[WeightMeasurementEnum.Serving] ?: 1f,
        validator = {
            between(
                min = 0f,
                max = 100f,
                onError = { MyError.Invalid }
            ) {
                positive(
                    onError = { MyError.Invalid }
                )
            }
        },
        parser = floatParser(
            onEmpty = { MyError.Empty },
            onNan = { MyError.Invalid }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    )

    val weightUnitInput = rememberFormFieldWithTextFieldValue(
        initialValue = suggestion.quantitySuggestions[WeightMeasurementEnum.WeightUnit] ?: 100f,
        validator = {
            between(
                min = 0f,
                max = 100_000f,
                onError = { MyError.Invalid }
            ) {
                positive(
                    onError = { MyError.Invalid }
                )
            }
        },
        parser = floatParser(
            onEmpty = { MyError.Empty },
            onNan = { MyError.Invalid }
        ),
        formatter = { "%.2f".format(Locale.ENGLISH, it).trimEnd('0').trimEnd('.') }
    )

    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        saver = Saver(
            save = {
                val latestMeasurement = it.latestMeasurement
                val enum = it.latestMeasurement.asEnum()

                when (latestMeasurement) {
                    is WeightMeasurement.Package -> listOf(enum, latestMeasurement.quantity)
                    is WeightMeasurement.Serving -> listOf(enum, latestMeasurement.quantity)
                    is WeightMeasurement.WeightUnit -> listOf(enum, latestMeasurement.weight)
                }
            },
            restore = {
                val enum = it[0] as WeightMeasurementEnum

                val measurement = when (enum) {
                    WeightMeasurementEnum.Package -> WeightMeasurement.Package(
                        it[1] as Float,
                        suggestion.product.packageWeight!!
                    )

                    WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(
                        it[1] as Float,
                        suggestion.product.servingWeight!!
                    )

                    WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(it[1] as Float)
                }

                PortionFormState(
                    product = suggestion.product,
                    servingInput = servingInput,
                    packageInput = packageInput,
                    weightUnitInput = weightUnitInput,
                    initialMeasurement = measurement,
                    coroutineScope = coroutineScope
                )
            }
        )
    ) {
        PortionFormState(
            product = suggestion.product,
            servingInput = servingInput,
            packageInput = packageInput,
            weightUnitInput = weightUnitInput,
            initialMeasurement = initialMeasurement,
            coroutineScope = coroutineScope
        )
    }
}

enum class MyError {
    Empty,
    Invalid
}

@Stable
class PortionFormState(
    val product: Product,
    val servingInput: FormFieldWithTextFieldValue<Float, MyError>,
    val packageInput: FormFieldWithTextFieldValue<Float, MyError>,
    val weightUnitInput: FormFieldWithTextFieldValue<Float, MyError>,
    initialMeasurement: WeightMeasurement,
    coroutineScope: CoroutineScope
) {
    /**
     * The weight of most recent input values in grams.
     */
    var latestMeasurement by mutableStateOf<WeightMeasurement>(initialMeasurement)
        private set

    init {
        // Update current weight when any of the inputs change
        coroutineScope.launch {
            snapshotFlow { servingInput.value }
                .drop(1)
                .collectLatest {
                    latestMeasurement = WeightMeasurement.Serving(it, product.servingWeight!!)
                }
        }

        coroutineScope.launch {
            snapshotFlow { packageInput.value }
                .drop(1)
                .collectLatest {
                    latestMeasurement = WeightMeasurement.Package(it, product.packageWeight!!)
                }
        }

        coroutineScope.launch {
            snapshotFlow { weightUnitInput.value }
                .drop(1)
                .collectLatest {
                    latestMeasurement = WeightMeasurement.WeightUnit(it)
                }
        }
    }
}
