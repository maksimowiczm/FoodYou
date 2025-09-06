package com.maksimowiczm.foodyou.infrastructure.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

val applicationCoroutineScopeQualifier = named(APPLICATION_COROUTINE_SCOPE)

fun Scope.applicationCoroutineScope(): CoroutineScope = get(applicationCoroutineScopeQualifier)
