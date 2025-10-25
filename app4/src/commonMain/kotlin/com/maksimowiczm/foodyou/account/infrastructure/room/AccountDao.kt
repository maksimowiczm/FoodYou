package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Query(
        """
        SELECT * 
        FROM Account
        WHERE id = :localAccountId
        """
    )
    abstract fun observeAccount(localAccountId: String): Flow<AccountEntity?>

    @Upsert protected abstract suspend fun upsertAccount(accountEntity: AccountEntity)

    @Query(
        """
        SELECT * 
        FROM AccountProfile
        WHERE localAccountId = :accountId
        """
    )
    abstract fun observeProfilesByAccountId(accountId: String): Flow<List<ProfileEntity>>

    @Upsert protected abstract suspend fun upsertProfile(profileEntity: ProfileEntity)

    @Query(
        """
        SELECT * 
        FROM AccountSettings
        WHERE accountId = :accountId
        """
    )
    abstract fun observeSettingsByAccountId(accountId: String): Flow<SettingsEntity?>

    @Upsert protected abstract suspend fun upsertSettings(settingsEntity: SettingsEntity)

    @Query("DELETE FROM AccountProfile WHERE localAccountId = :accountId")
    protected abstract suspend fun deleteProfiles(accountId: String)

    @Transaction
    open suspend fun upsertAccountWithDetails(
        accountEntity: AccountEntity,
        profileEntities: List<ProfileEntity>,
        settingsEntity: SettingsEntity,
    ) {
        upsertAccount(accountEntity)
        deleteProfiles(accountEntity.id)
        profileEntities.forEach { upsertProfile(it) }
        upsertSettings(settingsEntity)
    }
}
