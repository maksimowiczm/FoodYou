
package com.maksimowiczm.foodyou.ui.motion

import androidx.compose.animation.core.CubicBezierEasing

/**
 * @see [androidx.compose.animation.core.AnimationConstants]
 */
@Suppress("ktlint:standard:property-naming")
object AnimationConstantsExt {
    @Suppress("ConstPropertyName")
    const val EmphasizedDurationMillis: Int = 500
}

val EmphasisedEasing = CubicBezierEasing(0.208333f, 0.82f, 0.25f, 1.0f)
