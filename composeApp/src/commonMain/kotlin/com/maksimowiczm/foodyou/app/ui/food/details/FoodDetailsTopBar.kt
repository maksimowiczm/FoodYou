package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton

@Composable
internal fun FoodDetailsTopBar(
    onBack: () -> Unit,
    iconButtonContainerColor: Color,
    actions: @Composable RowScope.() -> Unit,
) {
    TopAppBar(
        title = {},
        navigationIcon = {
            ArrowBackIconButton(
                onClick = onBack,
                colors =
                    IconButtonDefaults.iconButtonColors(containerColor = iconButtonContainerColor),
            )
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
    )
}
