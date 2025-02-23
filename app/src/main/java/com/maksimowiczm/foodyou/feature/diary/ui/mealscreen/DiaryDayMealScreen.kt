package com.maksimowiczm.foodyou.feature.diary.ui.mealscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.maksimowiczm.foodyou.feature.addfood.data.model.Meal
import org.koin.androidx.compose.koinViewModel

@Composable
fun DiaryDayMealScreen(
    modifier: Modifier = Modifier,
    viewModel: DiaryDayMealViewModel = koinViewModel()
) {
    val diaryDay by viewModel.observeDiaryDay(viewModel.date).collectAsStateWithLifecycle(null)

    val meal = diaryDay?.meals?.first {
        it.id == viewModel.mealId
    }

    DiaryDayMealScreen(
        meal = meal ?: return,
        modifier = modifier
    )
}

@Composable
private fun DiaryDayMealScreen(meal: Meal, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Red
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            Text(
                text = meal.name
            )
        }
    }
}
