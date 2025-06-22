package com.maksimowiczm.foodyou.core.ui.nutrition

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.model.NutrientValue
import com.maksimowiczm.foodyou.core.model.NutritionFacts
import com.maksimowiczm.foodyou.core.model.NutritionFactsField
import com.maksimowiczm.foodyou.core.preferences.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.core.preferences.getBlocking
import com.maksimowiczm.foodyou.core.preferences.userPreference
import com.maksimowiczm.foodyou.core.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.core.ui.res.stringResource
import com.maksimowiczm.foodyou.core.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun NutritionFactsList(
    facts: NutritionFacts,
    modifier: Modifier = Modifier,
    incompleteValue: (
        NutrientValue.Incomplete
    ) -> (@Composable () -> Unit) = NutrientsListDefaults::incompleteValue,
    preference: NutritionFactsListPreference = userPreference()
) {
    val preferences by preference.collectAsStateWithLifecycle(preference.getBlocking())
    val order = preferences.orderedEnabled

    Column(modifier) {
        order.forEachIndexed { i, field ->
            NutrientListItem(
                facts = facts,
                field = field,
                incompleteValue = incompleteValue,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (i < order.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun CompactNutritionFactsList(
    facts: NutritionFacts,
    modifier: Modifier = Modifier,
    incompleteValue: (
        NutrientValue.Incomplete
    ) -> (@Composable () -> Unit) = NutrientsListDefaults::incompleteValue,
    preference: NutritionFactsListPreference = userPreference()
) {
    val allowedFields = setOf(
        NutritionFactsField.Energy,
        NutritionFactsField.Proteins,
        NutritionFactsField.Carbohydrates,
        NutritionFactsField.Fats
    )

    val preferences by preference.collectAsStateWithLifecycle(preference.getBlocking())
    val order = preferences.orderedEnabled.filter { it in allowedFields }

    Column(modifier) {
        order.forEachIndexed { i, field ->
            NutrientListItem(
                facts = facts,
                field = field,
                incompleteValue = incompleteValue,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            if (i < order.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun NutrientListItem(
    facts: NutritionFacts,
    field: NutritionFactsField,
    incompleteValue: (NutrientValue.Incomplete) -> (@Composable () -> Unit),
    modifier: Modifier = Modifier
) {
    val label = field.stringResource()
    val suffix = when (field) {
        NutritionFactsField.Energy -> stringResource(Res.string.unit_kcal)

        NutritionFactsField.Proteins,
        NutritionFactsField.Carbohydrates,
        NutritionFactsField.Sugars,
        NutritionFactsField.Fats,
        NutritionFactsField.SaturatedFats,
        NutritionFactsField.MonounsaturatedFats,
        NutritionFactsField.PolyunsaturatedFats,
        NutritionFactsField.Omega3,
        NutritionFactsField.Omega6,
        NutritionFactsField.Salt,
        NutritionFactsField.Fiber -> stringResource(Res.string.unit_gram_short)

        NutritionFactsField.Cholesterol,
        NutritionFactsField.Caffeine,
        NutritionFactsField.VitaminB1,
        NutritionFactsField.VitaminB2,
        NutritionFactsField.VitaminB3,
        NutritionFactsField.VitaminB5,
        NutritionFactsField.VitaminB6,
        NutritionFactsField.VitaminC,
        NutritionFactsField.VitaminE,
        NutritionFactsField.Manganese,
        NutritionFactsField.Magnesium,
        NutritionFactsField.Potassium,
        NutritionFactsField.Calcium,
        NutritionFactsField.Copper,
        NutritionFactsField.Zinc,
        NutritionFactsField.Sodium,
        NutritionFactsField.Iron,
        NutritionFactsField.Phosphorus -> stringResource(Res.string.unit_milligram_short)

        NutritionFactsField.VitaminA,
        NutritionFactsField.VitaminB7,
        NutritionFactsField.VitaminB9,
        NutritionFactsField.VitaminB12,
        NutritionFactsField.VitaminD,
        NutritionFactsField.VitaminK,
        NutritionFactsField.Selenium,
        NutritionFactsField.Iodine,
        NutritionFactsField.Chromium -> stringResource(Res.string.unit_microgram_short)
    }

    val nutrientsPalette = LocalNutrientsPalette.current

    val color = when (field) {
        NutritionFactsField.Proteins -> nutrientsPalette.proteinsOnSurfaceContainer
        NutritionFactsField.Carbohydrates -> nutrientsPalette.carbohydratesOnSurfaceContainer
        NutritionFactsField.Fats -> nutrientsPalette.fatsOnSurfaceContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    NutrientListItem(
        label = {
            Text(
                text = label,
                color = color
            )
        },
        value = {
            val summary = facts.get(field)

            when (summary) {
                is NutrientValue.Incomplete -> incompleteValue(summary)()
                is NutrientValue.Complete -> {
                    val value = summary.value.formatClipZeros()
                    Text("$value $suffix")
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun NutrientListItem(
    label: @Composable () -> Unit,
    value: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.bodyLarge
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
