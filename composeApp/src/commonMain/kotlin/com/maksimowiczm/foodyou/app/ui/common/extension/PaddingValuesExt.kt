package com.maksimowiczm.foodyou.app.ui.common.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable operator fun PaddingValues.plus(paddingValues: PaddingValues) = add(paddingValues)

/**
 * Adds the padding values together.
 *
 * @see PaddingValues.plus
 */
@Composable
fun PaddingValues.add(paddingValues: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return remember(this, paddingValues) {
        val start =
            paddingValues.calculateStartPadding(layoutDirection) +
                calculateStartPadding(layoutDirection)
        val top = paddingValues.calculateTopPadding() + calculateTopPadding()
        val end =
            paddingValues.calculateEndPadding(layoutDirection) +
                calculateEndPadding(layoutDirection)
        val bottom = paddingValues.calculateBottomPadding() + calculateBottomPadding()

        PaddingValues(start = start, top = top, end = end, bottom = bottom)
    }
}

/**
 * Adds the padding values together.
 *
 * @see PaddingValues.plus
 */
@Composable
fun PaddingValues.add(
    start: Dp = 0.dp,
    top: Dp = 0.dp,
    end: Dp = 0.dp,
    bottom: Dp = 0.dp,
): PaddingValues {
    val paddingValues =
        remember(this, start, top, end, bottom) {
            PaddingValues(start = start, top = top, end = end, bottom = bottom)
        }

    return add(paddingValues)
}

@Composable
fun PaddingValues.add(horizontal: Dp = 0.dp, vertical: Dp = 0.dp): PaddingValues {
    val paddingValues =
        remember(this, horizontal, vertical) {
            PaddingValues(horizontal = horizontal, vertical = vertical)
        }

    return add(paddingValues)
}

@Composable
fun PaddingValues.add(all: Dp): PaddingValues {
    val paddingValues = remember(this, all) { PaddingValues(all = all) }
    return add(paddingValues)
}

@Composable
fun PaddingValues.horizontal(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    return remember(this) {
        PaddingValues(
            start = calculateStartPadding(layoutDirection),
            end = calculateEndPadding(layoutDirection),
        )
    }
}

@Composable
fun PaddingValues.vertical(): PaddingValues =
    remember(this) { PaddingValues(top = calculateTopPadding(), bottom = calculateBottomPadding()) }
