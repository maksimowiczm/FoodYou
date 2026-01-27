package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AccountDao {
    @Upsert protected abstract suspend fun upsertAccount(accountEntity: AccountEntity)

    @Insert protected abstract suspend fun insertProfile(profileEntity: ProfileEntity)

    @Upsert protected abstract suspend fun upsertSettings(settingsEntity: SettingsEntity)

    @Query("DELETE FROM AccountProfile WHERE accountId = :accountId")
    protected abstract suspend fun deleteProfiles(accountId: String)

    @Query("DELETE FROM ProfileFavoriteFood WHERE accountId = :accountId")
    protected abstract suspend fun deleteFavoriteFoods(accountId: String)

    @Insert
    protected abstract suspend fun insertFavoriteFoods(
        favoriteFoods: List<ProfileFavoriteFoodEntity>
    )

    @Transaction
    open suspend fun upsertAccountWithDetails(
        accountEntity: AccountEntity,
        profileEntities: List<ProfileEntity>,
        //        profileFavoriteFoodEntities: List<ProfileFavoriteFoodEntity>,
        settingsEntity: SettingsEntity,
    ) {
        upsertAccount(accountEntity)
        deleteProfiles(accountEntity.id)
        profileEntities.forEach { insertProfile(it) }
        upsertSettings(settingsEntity)
        deleteFavoriteFoods(accountEntity.id)
        //        insertFavoriteFoods(profileFavoriteFoodEntities)
    }

    @Transaction
    @Query("SELECT a.id as a_id FROM Account a WHERE a.id = :id")
    abstract fun observeRichAccount(id: String): Flow<RichAccount?>

    @Transaction
    @Query("SELECT a.id as a_id FROM Account a")
    abstract fun observeRichAccounts(): Flow<List<RichAccount>>
}
