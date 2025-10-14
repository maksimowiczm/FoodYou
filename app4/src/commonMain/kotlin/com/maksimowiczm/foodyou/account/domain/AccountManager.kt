package com.maksimowiczm.foodyou.account.domain

import com.maksimowiczm.foodyou.common.Result
import com.maksimowiczm.foodyou.common.domain.LocalAccountId
import com.maksimowiczm.foodyou.common.domain.ProfileId
import kotlinx.coroutines.flow.Flow

interface AccountManager {
    suspend fun setPrimaryAccountId(accountId: LocalAccountId): Result<Unit, Error.AccountNotFound>

    /**
     * Flow of currently selected primary account id. It will emit null if no primary account is
     * set.
     */
    fun observePrimaryAccountId(): Flow<LocalAccountId?>

    /** Set the primary profile id for the currently selected primary account. */
    suspend fun setPrimaryProfileId(profileId: ProfileId): Result<Unit, Error>

    /**
     * Flow of currently selected primary profile id. It will block until a primary account is set.
     */
    fun observePrimaryProfileId(): Flow<ProfileId>

    sealed interface Error {
        data object AccountNotFound : Error

        data object ProfileNotFound : Error
    }
}
