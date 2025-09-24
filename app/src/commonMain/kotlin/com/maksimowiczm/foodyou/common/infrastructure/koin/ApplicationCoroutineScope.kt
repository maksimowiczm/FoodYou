package com.maksimowiczm.foodyou.common.infrastructure.koin

import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

private val applicationCoroutineScopeQualifier = named(APPLICATION_COROUTINE_SCOPE)

fun Module.applicationCoroutineScope(definition: Scope.(ParametersHolder) -> CoroutineScope) =
    single(applicationCoroutineScopeQualifier, false, definition)

fun Scope.applicationCoroutineScope(): CoroutineScope = get(applicationCoroutineScopeQualifier)
