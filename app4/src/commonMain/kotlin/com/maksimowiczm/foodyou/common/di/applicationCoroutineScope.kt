package com.maksimowiczm.foodyou.common.di

import kotlinx.coroutines.CoroutineScope
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

private const val APPLICATION_COROUTINE_SCOPE = "APPLICATION_COROUTINE_SCOPE"

private val applicationCoroutineScopeQualifier = named(APPLICATION_COROUTINE_SCOPE)

/**
 * Registers an application-scoped coroutine scope in the Koin module.
 *
 * This scope should be used for long-running operations that need to survive beyond individual
 * component lifecycles but should be cancelled when the application terminates.
 *
 * @param definition Factory function that creates the CoroutineScope
 * @return The configured single instance declaration
 */
fun Module.applicationCoroutineScope(definition: Scope.(ParametersHolder) -> CoroutineScope) =
    single(applicationCoroutineScopeQualifier, false, definition)

/**
 * Retrieves the application-scoped coroutine scope from Koin.
 *
 * @return The application-wide CoroutineScope instance
 */
fun Scope.applicationCoroutineScope(): CoroutineScope = get(applicationCoroutineScopeQualifier)
