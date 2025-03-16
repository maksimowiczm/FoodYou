package com.maksimowiczm.foodyou.ui.preview

import com.maksimowiczm.foodyou.data.model.DiaryDay
import com.maksimowiczm.foodyou.data.model.Meal
import com.maksimowiczm.foodyou.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.data.model.defaultGoals
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class DiaryDayPreviewParameterProvider : PreviewParameterProvider<DiaryDay> {
    private val productMap: Map<Meal, List<ProductWithWeightMeasurement>>
        get() {
            val products = ProductWithWeightMeasurementPreviewParameter().values.toList()
            val meals = MealsPreviewParameterProvider().values.toList()

            return meals.mapIndexed { i, m ->
                m to products.filterIndexed { index, _ -> index % 4 == i }
            }.toMap()
        }

    override val values: Sequence<DiaryDay> = sequenceOf(
        DiaryDay(
            date = LocalDate(2024, 12, 8),
            dailyGoals = defaultGoals(),
            mealProductMap = productMap
        )
    )
}
