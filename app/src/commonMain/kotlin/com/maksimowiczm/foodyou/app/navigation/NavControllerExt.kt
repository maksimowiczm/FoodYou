package com.maksimowiczm.foodyou.app.navigation

import androidx.navigation.NavController

fun <T : Any> NavController.navigateSingleTop(destination: T) {
    this.navigate(destination) { launchSingleTop = true }
}

inline fun <reified T : Any> NavController.popBackStackInclusive(): Boolean =
    this.popBackStack<T>(inclusive = true)
