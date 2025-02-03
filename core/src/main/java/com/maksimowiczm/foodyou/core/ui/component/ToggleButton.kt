package com.maksimowiczm.foodyou.core.ui.component

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewDynamicColors
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.core.ui.preview.BooleanPreviewParameter
import com.maksimowiczm.foodyou.core.ui.theme.FoodYouTheme

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    colors: ToggleButtonColors = ToggleButtonDefaults.colors(),
    indication: Indication? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    role = Role.Button
                }
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {
                        onCheckChange(!checked)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .drawBehind {
                        drawRect(
                            color = if (checked) colors.checkedColor else colors.uncheckedColor
                        )
                    }
                    .indication(
                        interactionSource = interactionSource,
                        indication = indication
                    )
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides if (checked) colors.checkedContentColor else colors.uncheckedColor
                ) {
                    content()
                }
            }
        }
    }
}

data class ToggleButtonColors(
    val checkedColor: Color,
    val checkedContentColor: Color,
    val uncheckedColor: Color
)

object ToggleButtonDefaults {
    @Composable
    fun colors(
        checkedColor: Color = MaterialTheme.colorScheme.primaryContainer,
        checkedContentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
        uncheckedColor: Color = MaterialTheme.colorScheme.secondaryContainer
    ) = ToggleButtonColors(
        checkedColor = checkedColor,
        checkedContentColor = checkedContentColor,
        uncheckedColor = uncheckedColor
    )
}

@PreviewDynamicColors
@Composable
private fun ToggleButtonColorsPreview(
    @PreviewParameter(BooleanPreviewParameter::class) checked: Boolean
) {
    FoodYouTheme {
        ToggleButton(
            checked = checked,
            onCheckChange = {}
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}
