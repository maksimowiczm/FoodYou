package com.maksimowiczm.foodyou.feature.home.ui.meals.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.business.settings.domain.NutrientsOrder
import com.maksimowiczm.foodyou.business.shared.domain.measurement.Measurement
import com.maksimowiczm.foodyou.feature.home.presentation.meals.card.MealEntryModel
import com.maksimowiczm.foodyou.shared.ui.res.formatClipZeros
import com.maksimowiczm.foodyou.shared.ui.theme.LocalNutrientsPalette
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun MealFoodListItem(
    entry: MealEntryModel,
    nutrientsOrder: List<NutrientsOrder>,
    color: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    val g = stringResource(Res.string.unit_gram_short)

    val proteinsString = entry.proteins?.let { it.formatClipZeros("%.1f") + " $g" }

    val carbohydratesString = entry.carbohydrates?.let { it.formatClipZeros("%.1f") + " $g" }

    val fatsString = entry.fats?.let { it.formatClipZeros("%.1f") + " $g" }

    val caloriesString = entry.caloriesString
    val measurementString = entry.measurementString

    if (measurementString == null) {
        FoodErrorListItem(
            headline = entry.name,
            errorMessage = stringResource(Res.string.error_measurement_error),
            modifier = modifier,
        )
    } else if (
        proteinsString == null ||
            carbohydratesString == null ||
            fatsString == null ||
            caloriesString == null
    ) {
        FoodErrorListItem(
            headline = entry.name,
            errorMessage = stringResource(Res.string.error_food_is_missing_required_fields),
            modifier = modifier,
        )
    } else {
        FoodListItem(
            name = { Text(entry.name) },
            proteins = { Text(text = proteinsString, style = MaterialTheme.typography.bodySmall) },
            carbohydrates = {
                Text(text = carbohydratesString, style = MaterialTheme.typography.bodySmall)
            },
            fats = { Text(text = fatsString, style = MaterialTheme.typography.bodySmall) },
            calories = { Text(text = caloriesString, style = MaterialTheme.typography.bodySmall) },
            measurement = {
                Text(text = measurementString, style = MaterialTheme.typography.bodySmall)
            },
            order = nutrientsOrder,
            modifier = modifier,
            containerColor = color,
            contentColor = contentColor,
            shape = shape,
            contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodListItem(
    name: @Composable () -> Unit,
    proteins: @Composable () -> Unit,
    carbohydrates: @Composable () -> Unit,
    fats: @Composable () -> Unit,
    calories: @Composable () -> Unit,
    measurement: @Composable () -> Unit,
    order: List<NutrientsOrder>,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    containerColor: Color = Color.Transparent,
    contentColor: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val nutrientsPalette = LocalNutrientsPalette.current

    val headlineContent =
        @Composable {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
            ) {
                name()
            }
        }

    val supportingContent =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.bodyMedium
                        ) {
                            calories()
                        }

                        order.forEach { field ->
                            when (field) {
                                NutrientsOrder.Proteins ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.proteinsOnSurfaceContainer,
                                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                                    ) {
                                        proteins()
                                    }

                                NutrientsOrder.Fats ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.fatsOnSurfaceContainer,
                                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                                    ) {
                                        fats()
                                    }

                                NutrientsOrder.Carbohydrates ->
                                    CompositionLocalProvider(
                                        LocalContentColor provides
                                            nutrientsPalette.carbohydratesOnSurfaceContainer,
                                        LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                                    ) {
                                        carbohydrates()
                                    }

                                NutrientsOrder.Other,
                                NutrientsOrder.Vitamins,
                                NutrientsOrder.Minerals -> Unit
                            }
                        }
                    }
                }
            }
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                measurement()
            }
        }

    val content =
        @Composable {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    headlineContent()
                    supportingContent()
                }

                trailingContent?.invoke()
            }
        }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            color = containerColor,
            contentColor = contentColor,
            shape = shape,
            content = content,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FoodErrorListItem(
    headline: String,
    errorMessage: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val content =
        @Composable {
            Column(
                modifier = Modifier.fillMaxWidth().padding(contentPadding),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
                ) {
                    Text(headline)
                }
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.bodyMedium
                ) {
                    Text(errorMessage)
                }
            }
        }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content,
        )
    }
}

private val MealEntryModel.measurementStringShort: String
    @Composable
    get() =
        with(measurement) {
            when (this) {
                is Measurement.Package ->
                    stringResource(
                        Res.string.x_times_y,
                        quantity.formatClipZeros(),
                        stringResource(Res.string.product_package),
                    )

                is Measurement.Serving ->
                    stringResource(
                        Res.string.x_times_y,
                        quantity.formatClipZeros(),
                        stringResource(Res.string.product_serving),
                    )

                is Measurement.Gram ->
                    "${value.formatClipZeros()} " + stringResource(Res.string.unit_gram_short)

                is Measurement.Milliliter ->
                    "${value.formatClipZeros()} " + stringResource(Res.string.unit_milliliter_short)
            }
        }

private val MealEntryModel.measurementString: String?
    @Composable
    get() {
        val short = measurementStringShort
        val weight = weight?.formatClipZeros() ?: return null
        val suffix =
            if (isLiquid) {
                stringResource(Res.string.unit_milliliter_short)
            } else {
                stringResource(Res.string.unit_gram_short)
            }

        return when (measurement) {
            is Measurement.Gram,
            is Measurement.Milliliter -> short

            is Measurement.Package,
            is Measurement.Serving -> "$short ($weight $suffix)"
        }
    }

private val MealEntryModel.caloriesString: String?
    @Composable
    get() {
        val value = energy ?: return null
        return "$value " + stringResource(Res.string.unit_kcal)
    }
