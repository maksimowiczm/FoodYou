package com.maksimowiczm.foodyou.core.preferences

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

interface UserPreference<T> {
    fun observe(): Flow<T>
    suspend fun set(value: T)
    suspend fun get(): T = observe().first()
}

fun <T> UserPreference<T>.setBlocking(value: T) = runBlocking { set(value) }
fun <T> UserPreference<T>.getBlocking(): T = runBlocking { get() }
