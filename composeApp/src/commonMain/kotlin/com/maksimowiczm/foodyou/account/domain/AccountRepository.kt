package com.maksimowiczm.foodyou.account.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

interface AccountRepository {

    /** Observes the current [Account], or `null` if no account exists yet. */
    fun observe(): Flow<Account?>

    /** Persists the given [account], replacing any previously stored data. */
    suspend fun save(account: Account)
}

/**
 * Applies [transform] to the current [Account] and persists the result.
 *
 * @throws IllegalStateException if no account exists.
 */
suspend fun AccountRepository.update(transform: Account.() -> Account): Account {
    val account = observe().first()
    checkNotNull(account) { "Can't update account which doesn't exist" }
    val updatedAccount = transform(account)
    save(updatedAccount)
    return updatedAccount
}
