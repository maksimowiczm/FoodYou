package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.NutrientValue
import com.maksimowiczm.foodyou.core.model.Nutrients
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.Res
import foodyou.app.generated.resources.not_available_short
import foodyou.app.generated.resources.nutriment_carbohydrates
import foodyou.app.generated.resources.nutriment_fats
import foodyou.app.generated.resources.nutriment_fiber
import foodyou.app.generated.resources.nutriment_proteins
import foodyou.app.generated.resources.nutriment_salt
import foodyou.app.generated.resources.nutriment_saturated_fats
import foodyou.app.generated.resources.nutriment_sodium
import foodyou.app.generated.resources.nutriment_sugars
import foodyou.app.generated.resources.unit_calories
import foodyou.app.generated.resources.unit_gram_short
import foodyou.app.generated.resources.unit_kcal
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutrientsList(
    nutrients: Nutrients,
    modifier: Modifier = Modifier,
    incompleteValue: (
        NutrientValue.Incomplete
    ) -> (@Composable () -> Unit) = NutrientsListDefaults::incompleteValue
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.titleMedium
    ) {
        Column(
            modifier = modifier
        ) {
            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.unit_calories)
                    )
                },
                value = {
                    val value = nutrients.calories.value.formatClipZeros()
                    val kcal = stringResource(Res.string.unit_kcal)
                    Text(text = "$value $kcal")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_proteins),
                        color = nutrientsPalette.proteinsOnSurfaceContainer
                    )
                },
                value = {
                    val value = nutrients.proteins.value.formatClipZeros()
                    val g = stringResource(Res.string.unit_gram_short)
                    Text(text = "$value $g")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_carbohydrates),
                        color = nutrientsPalette.carbohydratesOnSurfaceContainer
                    )
                },
                value = {
                    val value = nutrients.carbohydrates.value.formatClipZeros()
                    val g = stringResource(Res.string.unit_gram_short)
                    Text(text = "$value $g")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_sugars)
                    )
                },
                value = {
                    val summary = nutrients.sugars

                    when (summary) {
                        is NutrientValue.Incomplete -> incompleteValue(summary)()
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            val value = summary.value.formatClipZeros()
                            Text("$value $g")
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_fats),
                        color = nutrientsPalette.fatsOnSurfaceContainer
                    )
                },
                value = {
                    val value = nutrients.fats.value.formatClipZeros()
                    val g = stringResource(Res.string.unit_gram_short)
                    Text(text = "$value $g")
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_saturated_fats)
                    )
                },
                value = {
                    val summary = nutrients.saturatedFats
                    when (summary) {
                        is NutrientValue.Incomplete -> incompleteValue(summary)()
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            val value = summary.value.formatClipZeros()
                            Text("$value $g")
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_salt)
                    )
                },
                value = {
                    val summary = nutrients.salt
                    when (summary) {
                        is NutrientValue.Incomplete -> incompleteValue(summary)()
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            val value = summary.value.formatClipZeros()
                            Text("$value $g")
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_fiber)
                    )
                },
                value = {
                    val summary = nutrients.fiber
                    when (summary) {
                        is NutrientValue.Incomplete -> incompleteValue(summary)()
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            val value = summary.value.formatClipZeros()
                            Text("$value $g")
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider(Modifier.padding(horizontal = 48.dp))

            NutrientListItem(
                label = {
                    Text(
                        text = stringResource(Res.string.nutriment_sodium)
                    )
                },
                value = {
                    val summary = nutrients.sodium
                    when (summary) {
                        is NutrientValue.Incomplete -> incompleteValue(summary)()
                        is NutrientValue.Complete -> {
                            val g = stringResource(Res.string.unit_gram_short)
                            val value = summary.value.formatClipZeros()
                            Text("$value $g")
                        }
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun NutrientListItem(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier.weight(1f)
        ) {
            label()
        }
        value()
    }
}

object NutrientsListDefaults {
    val incompletePrefix: String
        @Composable get() = "*"

    fun incompleteValue(value: NutrientValue.Incomplete): @Composable () -> Unit = {
        val value = value.value?.formatClipZeros()

        val str = value?.let {
            "$incompletePrefix $it ${stringResource(Res.string.unit_gram_short)}"
        } ?: stringResource(Res.string.not_available_short)

        Text(
            text = str,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
