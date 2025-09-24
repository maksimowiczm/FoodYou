package com.maksimowiczm.foodyou.app.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.*
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.maksimowiczm.foodyou.app.ui.common.motion.materialSharedAxisXIn
import com.maksimowiczm.foodyou.app.ui.common.motion.materialSharedAxisXOut
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KType

/**
 * A composable that animates forward and backward navigation with shared axis X transitions.
 *
 * @see <a
 *   href="https://m3.material.io/styles/motion/transitions/transition-patterns#df9c7d76-1454-47f3-ad1c-268a31f58bad">Forward
 *   and backward</a>
 */
inline fun <reified T : Any> NavGraphBuilder.forwardBackwardComposable(
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    deepLinks: List<NavDeepLink> = emptyList(),
    noinline enterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            EnterTransition?)? =
        {
            ForwardBackwardComposableDefaults.enterTransition()
        },
    noinline exitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            ExitTransition?)? =
        {
            ForwardBackwardComposableDefaults.exitTransition()
        },
    noinline popEnterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            EnterTransition?)? =
        {
            ForwardBackwardComposableDefaults.popEnterTransition()
        },
    noinline popExitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            ExitTransition?)? =
        {
            ForwardBackwardComposableDefaults.popExitTransition()
        },
    noinline sizeTransform:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            SizeTransform?)? =
        null,
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable<T>(
        typeMap = typeMap,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        content = content,
    )
}

fun NavGraphBuilder.forwardBackwardComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            EnterTransition?)? =
        {
            ForwardBackwardComposableDefaults.enterTransition()
        },
    exitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            ExitTransition?)? =
        {
            ForwardBackwardComposableDefaults.exitTransition()
        },
    popEnterTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            EnterTransition?)? =
        {
            ForwardBackwardComposableDefaults.popEnterTransition()
        },
    popExitTransition:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            ExitTransition?)? =
        {
            ForwardBackwardComposableDefaults.popExitTransition()
        },
    sizeTransform:
        (AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards
            SizeTransform?)? =
        null,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        deepLinks = deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        sizeTransform = sizeTransform,
        content = content,
    )
}

object ForwardBackwardComposableDefaults {
    const val INITIAL_OFFSET_FACTOR = 0.10f

    fun enterTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXIn(initialOffsetX = { (it * offset).toInt() })

    fun exitTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXOut(targetOffsetX = { -(it * offset).toInt() })

    fun popEnterTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXIn(initialOffsetX = { -(it * offset).toInt() })

    fun popExitTransition(offset: Float = INITIAL_OFFSET_FACTOR) =
        materialSharedAxisXOut(targetOffsetX = { (it * offset).toInt() })
}
