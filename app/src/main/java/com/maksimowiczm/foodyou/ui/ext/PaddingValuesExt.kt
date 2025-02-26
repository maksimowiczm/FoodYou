package com.maksimowiczm.foodyou.ui.ext

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues) = add(paddingValues)

/**
 * Adds the padding values together.
 *
 * @see PaddingValues.plus
 */
@Composable
fun PaddingValues.add(paddingValues: PaddingValues): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current

    val start =
        paddingValues.calculateStartPadding(layoutDirection) +
            calculateStartPadding(layoutDirection)
    val top = paddingValues.calculateTopPadding() + calculateTopPadding()
    val end =
        paddingValues.calculateEndPadding(layoutDirection) + calculateEndPadding(layoutDirection)
    val bottom = paddingValues.calculateBottomPadding() + calculateBottomPadding()

    return PaddingValues(
        start = start,
        top = top,
        end = end,
        bottom = bottom
    )
}
