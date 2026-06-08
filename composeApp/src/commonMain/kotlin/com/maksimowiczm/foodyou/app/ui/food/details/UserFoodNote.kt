package com.maksimowiczm.foodyou.app.ui.food.details

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.utility.LocalClipboardManager

@Composable
internal fun UserFoodNote(note: String, modifier: Modifier = Modifier) {
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier =
            modifier.combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = {},
                onLongClick = { clipboardManager.copy("note", note) },
            ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(text = note, style = MaterialTheme.typography.bodyMedium)
    }
}
