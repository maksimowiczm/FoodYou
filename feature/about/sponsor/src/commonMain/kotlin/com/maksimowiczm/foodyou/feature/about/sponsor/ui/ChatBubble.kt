package com.maksimowiczm.foodyou.feature.about.sponsor.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun ChatBubble(
    icon: (@Composable () -> Unit)?,
    author: String?,
    authorExtra: String?,
    message: String?,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
) {
    ChatBubble(
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (icon != null || author != null || authorExtra != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        icon()
                        Spacer(Modifier.width(16.dp))
                    }

                    if (author != null) {
                        Text(text = author, style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.width(8.dp))
                    }

                    if (authorExtra != null) {
                        Text(
                            text = authorExtra,
                            style =
                                if (authorExtra.length > 20) {
                                    MaterialTheme.typography.bodySmall
                                } else {
                                    MaterialTheme.typography.bodyMedium
                                },
                        )
                    }
                }
            }

            if (message != null) {
                Text(text = message, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
internal fun ChatBubble(
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        content = content,
    )
}

@Composable
internal fun ChatBubbleSkeleton(
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shimmer: Shimmer = rememberShimmer(ShimmerBounds.View),
) {
    ChatBubble(
        icon = null,
        author = null,
        authorExtra = null,
        message = "",
        shape = shape,
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier.shimmer(shimmer),
    )
}

internal object ChatBubbleDefaults {
    val iconSize: Dp
        get() = 24.dp

    val receivedContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainer

    val receivedContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface

    val sentContainerColor: Color
        @Composable get() = MaterialTheme.colorScheme.secondaryContainer

    val sentContentColor: Color
        @Composable get() = MaterialTheme.colorScheme.onSecondaryContainer
}
