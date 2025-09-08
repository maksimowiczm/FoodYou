package com.maksimowiczm.foodyou.shared.domain.database

interface TransactionProvider {

    /**
     * Executes the given block of code within a database transaction.
     *
     * @param block The block of code to execute within the transaction.
     */
    suspend fun <T> withTransaction(block: suspend TransactionScope<T>.() -> T): T
}
