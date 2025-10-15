package com.maksimowiczm.foodyou.app.ui.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.app.ui.common.theme.PreviewFoodYouTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FoodErrorListItem(
    headline: String,
    image: @Composable (() -> Unit)?,
    errorMessage: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: Shape = RectangleShape,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
) {
    val content =
        @Composable {
            Row(
                modifier = Modifier.padding(contentPadding),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                image?.invoke()
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleMediumEmphasized
                    ) {
                        Text(headline)
                    }
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.bodyMedium
                    ) {
                        Text(errorMessage)
                    }
                }
            }
        }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content,
        )
    } else {
        Surface(
            modifier = modifier,
            shape = shape,
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            content = content,
        )
    }
}

@Preview
@Composable
private fun FoodErrorListItemPreview() {
    PreviewFoodYouTheme {
        FoodErrorListItem(
            headline = "Sample Food",
            image = null,
            errorMessage = "This food item is missing required fields.",
        )
    }
}
