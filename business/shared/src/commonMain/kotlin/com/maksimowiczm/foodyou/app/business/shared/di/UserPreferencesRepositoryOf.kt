package com.maksimowiczm.foodyou.app.business.shared.di

import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferences
import com.maksimowiczm.foodyou.shared.domain.userpreferences.UserPreferencesRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.new
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

inline fun <reified P : UserPreferences> Scope.userPreferencesRepository(
    qualifier: Qualifier = named(P::class.qualifiedName!!)
): UserPreferencesRepository<P> = get(qualifier)

inline fun <reified P : UserPreferences> Module.userPreferencesRepository(
    qualifier: Qualifier = named(P::class.qualifiedName!!),
    noinline definition: Scope.(ParametersHolder) -> UserPreferencesRepository<P>,
) = factory(qualifier, definition)

inline fun <reified P : UserPreferences> Module.userPreferencesRepositoryOf(
    crossinline constructor: () -> UserPreferencesRepository<P>,
    qualifier: Qualifier = named(P::class.qualifiedName!!),
) = userPreferencesRepository(qualifier) { new(constructor) }

inline fun <reified P : UserPreferences, reified T1> Module.userPreferencesRepositoryOf(
    crossinline constructor: (T1) -> UserPreferencesRepository<P>,
    qualifier: Qualifier = named(P::class.qualifiedName!!),
) = userPreferencesRepository(qualifier) { new(constructor) }

inline fun <reified P : UserPreferences, reified T1, reified T2> Module.userPreferencesRepositoryOf(
    crossinline constructor: (T1, T2) -> UserPreferencesRepository<P>,
    qualifier: Qualifier = named(P::class.qualifiedName!!),
) = userPreferencesRepository(qualifier) { new(constructor) }

inline fun <reified P : UserPreferences, reified T1, reified T2, reified T3> Module
    .userPreferencesRepositoryOf(
    crossinline constructor: (T1, T2, T3) -> UserPreferencesRepository<P>,
    qualifier: Qualifier = named(P::class.qualifiedName!!),
) = userPreferencesRepository(qualifier) { new(constructor) }

inline fun <reified P : UserPreferences, reified T1, reified T2, reified T3, reified T4> Module
    .userPreferencesRepositoryOf(
    crossinline constructor: (T1, T2, T3, T4) -> UserPreferencesRepository<P>,
    qualifier: Qualifier = named(P::class.qualifiedName!!),
) = userPreferencesRepository(qualifier) { new(constructor) }
