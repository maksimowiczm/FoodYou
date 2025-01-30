package com.maksimowiczm.foodyou.feature.diary.ui.nutriments

import android.content.res.Configuration
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.R
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.feature.diary.ui.theme.LocalNutrimentsPalette
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme

@Composable
fun NutrimentsRowCard(
    diaryDay: DiaryDay,
    modifier: Modifier = Modifier,
    spacer: @Composable () -> Unit = { Spacer(Modifier.width(8.dp)) },
    startItem: @Composable () -> Unit = spacer,
    endItem: @Composable () -> Unit = spacer
) {
    val nutrimentsPalette = LocalNutrimentsPalette.current

    // Use Row instead of LazyRow to allow intrinsic height calculation
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .height(IntrinsicSize.Min)
    ) {
        startItem()

        NutrimentCard(
            text = stringResource(R.string.nutriment_proteins),
            value = diaryDay.totalProteins,
            goalValue = diaryDay.dailyGoals.proteinsAsGrams,
            color = nutrimentsPalette.proteinsOnSurfaceContainer,
            modifier = Modifier.fillMaxHeight()
        )

        spacer()

        NutrimentCard(
            text = stringResource(R.string.nutriment_carbohydrates),
            value = diaryDay.totalCarbohydrates,
            color = nutrimentsPalette.carbohydratesOnSurfaceContainer,
            goalValue = diaryDay.dailyGoals.carbohydratesAsGrams,
            modifier = Modifier.fillMaxHeight()
        )

        spacer()

        NutrimentCard(
            text = stringResource(R.string.nutriment_fats),
            value = diaryDay.totalFats,
            color = nutrimentsPalette.fatsOnSurfaceContainer,
            goalValue = diaryDay.dailyGoals.fatsAsGrams,
            modifier = Modifier.fillMaxHeight()
        )

//        spacer()

//        ElevatedCard(
//            modifier = Modifier.fillMaxHeight()
//        ) {
//            Column(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .clickable { },
//                verticalArrangement = Arrangement.Center
//            ) {
//                Box(
//                    modifier = Modifier.size(48.dp),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.Settings,
//                        contentDescription = null
//                    )
//                }
//            }
//        }

        endItem()
    }
}

private const val infiniteWidthDevice = "spec:width=1000dp,height=891dp,dpi=400"

@Preview(
    device = infiniteWidthDevice
)
@Preview(
    device = infiniteWidthDevice,
    fontScale = 1.5f
)
@Preview(
    device = infiniteWidthDevice,
    fontScale = 2f
)
@Preview(
    device = infiniteWidthDevice,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun NutrimentsCardPreview(
    @PreviewParameter(DiaryDayPreviewParameterProvider::class) diaryDay: DiaryDay
) {
    FoodYouTheme {
        NutrimentsRowCard(
            diaryDay = diaryDay
        )
    }
}
