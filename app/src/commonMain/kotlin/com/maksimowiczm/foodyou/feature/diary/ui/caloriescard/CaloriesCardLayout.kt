package com.maksimowiczm.foodyou.feature.diary.ui.caloriescard

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.ui.home.FoodYouHomeCard

@Composable
fun CaloriesCardLayout(
    state: CaloriesCardState,
    header: @Composable ColumnScope.() -> Unit,
    compactContent: @Composable ColumnScope.() -> Unit,
    expandedContent: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    FoodYouHomeCard(
        modifier = modifier.animateContentSize()
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

object CaloriesCardLayoutDefaults {
    @Composable
    fun Header(
        state: CaloriesCardState,
        onToggleState: () -> Unit,
        modifier: Modifier = Modifier,
        title: @Composable () -> Unit
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            title()

            Spacer(Modifier.weight(1f))

            IconButton(
                onClick = onToggleState
            ) {
                Icon(
                    imageVector = when (state) {
                        CaloriesCardState.Compact -> Icons.Default.UnfoldLess
                        CaloriesCardState.Expanded -> Icons.Default.UnfoldMore
                    },
                    contentDescription = null
                )
            }
        }
    }
}
