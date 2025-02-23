package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.diary.data.model.DiaryDay
import com.maksimowiczm.foodyou.feature.diary.ui.previewparameter.DiaryDayPreviewParameterProvider
import com.maksimowiczm.foodyou.ui.theme.FoodYouTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun DiaryDayMealScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryDayMealViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(viewModel.date).collectAsStateWithLifecycle(null)

    if (diaryDay == null) {
        return
    } else {
        DiaryDayMealScreen(
            diaryDay = diaryDay!!,
            modifier = modifier
        )
    }
}

@Composable
private fun DiaryDayMealScreen(
    diaryDay: DiaryDay,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(diaryDay.toString())
        }
    }
}

@Preview
@Composable
private fun DiaryDayMealScreenPreview() {
    FoodYouTheme {
        DiaryDayMealScreen(
            diaryDay = DiaryDayPreviewParameterProvider().values.first()
        )
    }
}