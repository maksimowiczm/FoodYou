package com.maksimowiczm.foodyou.capabilities.foodbrowsing

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun FoodSearchErrorCard(
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDetails by rememberSaveable { mutableStateOf(false) }
    val message = message ?: stringResource(Res.string.error_unknown_error)

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
            Text(
                text = stringResource(Res.string.neutral_an_error_occurred),
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.neutral_remote_database_error),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                TextButton(
                    onClick = { showDetails = !showDetails },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                ) {
                    Text(stringResource(Res.string.action_show_details))
                }

                FilledTonalButton(
                    onClick = { onRetry() },
                    shapes = ButtonDefaults.shapes(),
                    colors =
                        ButtonDefaults.filledTonalButtonColors(
                            contentColor = MaterialTheme.colorScheme.errorContainer,
                            containerColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                ) {
                    Text(stringResource(Res.string.action_retry))
                }
            }
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = showDetails,
                enter =
                    fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                        expandVertically(MaterialTheme.motionScheme.defaultSpatialSpec()),
                exit =
                    fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) +
                        shrinkVertically(MaterialTheme.motionScheme.defaultSpatialSpec()),
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
        }
    }
}
