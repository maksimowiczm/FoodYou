package com.maksimowiczm.foodyou.foodyou3.application

import io.github.vinceglb.filekit.PlatformFile

fun interface FoodYou3DataScopeFactory {
    suspend fun create(file: PlatformFile): FoodYou3DataScope
}
