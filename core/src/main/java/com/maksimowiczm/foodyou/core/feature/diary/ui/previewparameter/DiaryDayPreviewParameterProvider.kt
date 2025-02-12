package com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.data.model.defaultGoals
import kotlinx.datetime.LocalDate

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
