package com.maksimowiczm.foodyou.ui.preview

import com.maksimowiczm.foodyou.data.model.Meal
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class MealsPreviewParameterProvider : PreviewParameterProvider<Meal> {
    override val values: Sequence<Meal> = sequenceOf(
        Meal(
            id = 1,
            name = "Breakfast",
            from = LocalTime(6, 0),
            to = LocalTime(10, 0),
            lexoRank = "a"
        ),
        Meal(
            id = 2,
            name = "Lunch",
            from = LocalTime(12, 0),
            to = LocalTime(16, 0),
            lexoRank = "a"
        ),
        Meal(
            id = 3,
            name = "Dinner",
            from = LocalTime(18, 0),
            to = LocalTime(22, 0),
            lexoRank = "a"
        ),
        Meal(
            id = 4,
            name = "Snacks",
            from = LocalTime(10, 0),
            to = LocalTime(12, 0),
            lexoRank = "a"
        )
    )
}
