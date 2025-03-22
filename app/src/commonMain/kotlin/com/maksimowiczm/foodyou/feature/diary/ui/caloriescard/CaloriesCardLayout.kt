package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCard

@Composable
fun CaloriesCardLayout(
    state: CaloriesCardState,
    toggleState: () -> Unit,
    header: @Composable ColumnScope.() -> Unit,
    compactContent: @Composable ColumnScope.() -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        modifier = modifier.animateContentSize(),
        onClick = toggleState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(16.dp)
        ) {
            header()

            Spacer(Modifier.height(8.dp))

            compactContent()

            if (state == CaloriesCardState.Expanded) {
                Spacer(Modifier.height(16.dp))
                expandedContent()
            }
        }
    }
}
