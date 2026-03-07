package com.maksimowiczm.foodyou.app.ui.home.calendar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun CalendarCardPersonalizationCard(
    containerColor: Color,
    contentColor: Color,
    shadowElevation: Dp,
    modifier: Modifier,
    dragHandle: @Composable (() -> Unit),
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = shadowElevation,
        shape = CardDefaults.shape,
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Outlined.CalendarMonth, contentDescription = null)
            }
            Spacer(Modifier.width(16.dp))
            Text(stringResource(Res.string.headline_calendar))
            Spacer(Modifier.weight(1f))
            dragHandle()
        }
    }
}
