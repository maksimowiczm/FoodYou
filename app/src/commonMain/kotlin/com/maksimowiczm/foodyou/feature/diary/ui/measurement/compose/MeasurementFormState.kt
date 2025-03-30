package com.maksimowiczm.foodyou.feature.diary.ui.measurement.compose

import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import com.maksimowiczm.foodyou.feature.diary.data.model.MeasurementSuggestion
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurement
import com.maksimowiczm.foodyou.feature.diary.data.model.WeightMeasurementEnum
import com.maksimowiczm.foodyou.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.ui.simpleform.FormField
import com.maksimowiczm.foodyou.ui.simpleform.ParseResult
import com.maksimowiczm.foodyou.ui.simpleform.rememberFormField
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

@Composable
fun rememberMeasurementFormState(
    suggestion: MeasurementSuggestion,
    highlight: WeightMeasurementEnum? = null,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): MeasurementFormState {
    val packagee = suggestion.packageSuggestion?.let { measurement ->
        rememberFormField(
            initialValue = measurement.quantity,
            parser = { str ->
                val f = str.toFloatOrNull()

                when (f) {
                    null -> ParseResult.Failure(Unit)
                    else if (f <= 0 || f > 9999) -> ParseResult.Failure(Unit)
                    else -> ParseResult.Success(str.toFloat())
                }
            },
            textFieldState = rememberTextFieldState(
                initialText = measurement.quantity.formatClipZeros()
            )
        )
    }

    val serving = suggestion.servingSuggestion?.let { measurement ->
        rememberFormField(
            initialValue = measurement.quantity,
            parser = { str ->
                val f = str.toFloatOrNull()

                when (f) {
                    null -> ParseResult.Failure(Unit)
                    else if (f <= 0 || f > 9999) -> ParseResult.Failure(Unit)
                    else -> ParseResult.Success(str.toFloat())
                }
            },
            textFieldState = rememberTextFieldState(
                initialText = measurement.quantity.formatClipZeros()
            )
        )
    }

    val weight = rememberFormField(
        initialValue = suggestion.weightSuggestion.weight,
        parser = { str ->
            val f = str.toFloatOrNull()

            when (f) {
                null -> ParseResult.Failure(Unit)
                else if (f <= 0 || f > 9999) -> ParseResult.Failure(Unit)
                else -> ParseResult.Success(str.toFloat())
            }
        },
        textFieldState = rememberTextFieldState(
            initialText = suggestion.weightSuggestion.weight.formatClipZeros()
        )
    )

    return rememberSaveable(
        packagee,
        serving,
        weight,
        highlight,
        saver = Saver(
            save = {
                val latestMeasurement = it.latestWeightMeasurement
                val enum = it.latestWeightMeasurement.asEnum()

                when (latestMeasurement) {
                    is WeightMeasurement.Package -> listOf(enum, latestMeasurement.quantity)
                    is WeightMeasurement.Serving -> listOf(enum, latestMeasurement.quantity)
                    is WeightMeasurement.WeightUnit -> listOf(enum, latestMeasurement.weight)
                }
            },
            restore = {
                val enum = it[0] as WeightMeasurementEnum

                val measurement = when (enum) {
                    WeightMeasurementEnum.Package -> WeightMeasurement.Package(it[1] as Float)
                    WeightMeasurementEnum.Serving -> WeightMeasurement.Serving(it[1] as Float)
                    WeightMeasurementEnum.WeightUnit -> WeightMeasurement.WeightUnit(it[1] as Float)
                }

                MeasurementFormState(
                    packageField = packagee,
                    servingField = serving,
                    weightField = weight,
                    coroutineScope = coroutineScope,
                    initialHighlight = highlight,
                    initialMeasurement = measurement
                )
            }
        )
    ) {
        MeasurementFormState(
            packageField = packagee,
            servingField = serving,
            weightField = weight,
            coroutineScope = coroutineScope,
            initialHighlight = highlight,
            initialMeasurement = WeightMeasurement.WeightUnit(100f)
        )
    }
}

@Stable
class MeasurementFormState(
    val packageField: FormField<Float, Unit>?,
    val servingField: FormField<Float, Unit>?,
    val weightField: FormField<Float, Unit>,
    coroutineScope: CoroutineScope,
    initialHighlight: WeightMeasurementEnum?,
    initialMeasurement: WeightMeasurement
) {
    var highlight by mutableStateOf(initialHighlight)

    var latestWeightMeasurement by mutableStateOf(initialMeasurement)
        private set

    init {
        coroutineScope.launch {
            snapshotFlow { packageField?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .collectLatest {
                    latestWeightMeasurement = WeightMeasurement.Package(it)
                }
        }

        coroutineScope.launch {
            snapshotFlow { servingField?.value }
                .filterNotNull()
                .distinctUntilChanged()
                .drop(1)
                .collectLatest {
                    latestWeightMeasurement = WeightMeasurement.Serving(it)
                }
        }

        coroutineScope.launch {
            snapshotFlow { weightField.value }
                .distinctUntilChanged()
                .drop(1)
                .collectLatest {
                    latestWeightMeasurement = WeightMeasurement.WeightUnit(it)
                }
        }
    }
}
