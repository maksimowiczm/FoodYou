package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material3.MotionScheme

object ForwardBackwardTransition {
    const val INITIAL_OFFSET_FACTOR = 0.15f

    fun enterTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXIn(initialOffsetX = { (it * offset).toInt() })

    fun exitTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXOut(targetOffsetX = { -(it * offset).toInt() })

    fun popEnterTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        scaleIn(
            animationSpec = MotionScheme.expressive().defaultSpatialSpec(),
            initialScale = 0.9f,
        ) + materialSharedAxisXIn(initialOffsetX = { -(it * offset).toInt() })

    fun popExitTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        scaleOut(
            animationSpec = MotionScheme.expressive().defaultSpatialSpec(),
            targetScale = 0.9f,
        ) + materialSharedAxisXOut(targetOffsetX = { (it * offset).toInt() })
}
