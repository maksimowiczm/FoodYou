package com.maksimowiczm.foodyou.capabilities.food

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.ui.utility.LocalClipboardManager

interface FoodUiScopeWithNote {
    val note: String?
}

@Composable
fun FoodUiScopeWithNote.Note(modifier: Modifier = Modifier) {
    val note = note ?: return
    val clipboardManager = LocalClipboardManager.current

    Box(
        modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .combinedClickable(
                interactionSource = null,
                indication = null,
                onClick = {},
                onLongClick = { clipboardManager.copy("note", note) },
            )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = note, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
