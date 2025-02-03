package com.maksimowiczm.foodyou.core.feature.diary.ui.previewparameter

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.Meal
import com.maksimowiczm.foodyou.core.feature.addfood.data.model.ProductWithWeightMeasurement
import com.maksimowiczm.foodyou.core.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.core.feature.diary.data.model.defaultGoals
import java.time.LocalDate
import java.util.Calendar

class DiaryDayPreviewParameterProvider : PreviewParameterProvider<DiaryDay> {
    private val productMap: Map<Meal, List<ProductWithWeightMeasurement>>
        get() {
            val products = ProductWithWeightMeasurementPreviewParameter().values.toList()

            return Meal.entries.mapIndexed { index, meal ->
                meal to products.filterIndexed { i, _ -> i != index }
            }.toMap()
        }

    override val values: Sequence<DiaryDay> = sequenceOf(
        DiaryDay(
            date = LocalDate.of(2024, Calendar.DECEMBER, 8),
            dailyGoals = defaultGoals(),
            mealProductMap = productMap
        )
    )
}
