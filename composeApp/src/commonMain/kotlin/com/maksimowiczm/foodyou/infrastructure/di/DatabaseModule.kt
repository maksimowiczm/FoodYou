package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.infrastructure.database.OpenSourceDatabase
import org.koin.core.module.Module
import org.koin.core.scope.Scope

fun Scope.database(): OpenSourceDatabase = get()

expect val databaseModule: Module
