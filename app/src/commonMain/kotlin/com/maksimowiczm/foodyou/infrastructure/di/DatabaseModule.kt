package com.maksimowiczm.foodyou.infrastructure.di

import org.koin.core.module.Module

/**
 * Legacy 1.*.* database name
 */
@Suppress("unused")
const val DATABASE_NAME_1 = "open_source_database.db"

const val DATABASE_NAME_2 = "food_you_database.db"
const val DATABASE_NAME = DATABASE_NAME_2

expect val databaseModule: Module
