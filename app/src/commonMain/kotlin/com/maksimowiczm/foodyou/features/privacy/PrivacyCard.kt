package com.maksimowiczm.foodyou.features.privacy

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.maksimowiczm.foodyou.shared.ui.InteractionShapes
import com.maksimowiczm.foodyou.shared.ui.rememberAnimatedShape
import com.maksimowiczm.foodyou.shared.ui.rememberInteractionAnimatedShape
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun PrivacyCard(
    selected: Boolean,
    title: @Composable () -> Unit,
    shapes: InteractionShapes,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PrivacyCardDefaults.contentPadding,
    color: Color = PrivacyCardDefaults.color(selected),
    contentColor: Color = PrivacyCardDefaults.contentColor(selected),
    interactionSource: MutableInteractionSource? = null,
    content: @Composable PrivacyCardScope.() -> Unit,
) {
    val scope = remember(selected) { PrivacyCardScope(selected) }

    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val animatedShape =
        rememberInteractionAnimatedShape(
            shapes = shapes,
            interactionSource = interactionSource,
        )

    val inner =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(Modifier.padding(contentPadding)) {
                    title()
                    Spacer(Modifier.height(8.dp))
                    content(scope)
                }
            }
        }

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = animatedShape,
        color = color,
        contentColor = contentColor,
        interactionSource = interactionSource,
        content = inner,
    )
}

@Composable
fun PrivacyCard(
    title: @Composable () -> Unit,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PrivacyCardDefaults.contentPadding,
    color: Color = PrivacyCardDefaults.color(true),
    contentColor: Color = PrivacyCardDefaults.contentColor(true),
    scope: PrivacyCardScope = remember { PrivacyCardScope(true) },
    content: @Composable PrivacyCardScope.() -> Unit,
) {
    val animatedShape =
        when (shape) {
            is CornerBasedShape ->
                rememberAnimatedShape(
                    shape,
                    MaterialTheme.motionScheme.fastSpatialSpec(),
                )
            else -> shape
        }

    val inner =
        @Composable {
            CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyMedium) {
                Column(Modifier.padding(contentPadding)) {
                    title()
                    Spacer(Modifier.height(8.dp))
                    content(scope)
                }
            }
        }

    Surface(
        modifier = modifier,
        shape = animatedShape,
        color = color,
        contentColor = contentColor,
        content = inner,
    )
}

class PrivacyCardScope(val selected: Boolean) {
    @Composable
    fun Chip(
        onClick: () -> Unit,
        label: @Composable () -> Unit,
        enabled: Boolean = true,
        leadingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        colors: ChipColors =
            AssistChipDefaults.assistChipColors(
                labelColor = PrivacyCardDefaults.contentColor(selected),
                leadingIconContentColor = PrivacyCardDefaults.iconColor(selected),
                trailingIconContentColor = PrivacyCardDefaults.iconColor(selected),
            ),
        border: BorderStroke? =
            AssistChipDefaults.assistChipBorder(
                enabled = true,
                borderColor =
                    if (selected) MaterialTheme.colorScheme.inversePrimary
                    else MaterialTheme.colorScheme.outlineVariant,
            ),
    ) {
        AssistChip(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            leadingIcon = leadingIcon,
            label = label,
            colors = colors,
            border = border,
        )
    }
}

object PrivacyCardDefaults {
    val contentPadding = PaddingValues(16.dp)

    @Composable
    fun shape(index: Int, count: Int, selected: Boolean): CornerBasedShape {
        return if (selected) MaterialTheme.shapes.large
        else if (index == 0)
            MaterialTheme.shapes.extraSmall.copy(
                topStart = MaterialTheme.shapes.large.topStart,
                topEnd = MaterialTheme.shapes.large.topEnd,
            )
        else if (index == count - 1)
            (MaterialTheme.shapes.extraSmall).copy(
                bottomStart = MaterialTheme.shapes.large.topStart,
                bottomEnd = MaterialTheme.shapes.large.topEnd,
            )
        else MaterialTheme.shapes.extraSmall
    }

    @Composable
    fun shapes(index: Int, count: Int, selected: Boolean): InteractionShapes {
        val base = shape(index, count, selected)
        val pressed = MaterialTheme.shapes.large
        return InteractionShapes(
            shape = base,
            pressedShape = pressed,
        )
    }

    @Composable
    fun color(selected: Boolean): Color =
        if (selected) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceContainer

    @Composable fun contentColor(selected: Boolean): Color = contentColorFor(color(selected))

    @Composable
    fun iconColor(selected: Boolean): Color =
        if (selected) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.primary
}

@Composable
fun PrivacyCardScope.PrivacyPolicyChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.PrivacyTip,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_privacy_policy)) },
    )
}

@Composable
fun PrivacyCardScope.TermsOfUseChip(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Chip(
        onClick = onClick,
        modifier = modifier,
        leadingIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                contentDescription = null,
                modifier = Modifier.size(AssistChipDefaults.IconSize),
            )
        },
        label = { Text(stringResource(Res.string.headline_terms_of_use)) },
    )
}
