package com.maksimowiczm.foodyou.account.infrastructure.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal abstract class AccountDao {
    @Query("SELECT * FROM AccountProfile") abstract fun observeProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM ProfileFavoriteFood")
    abstract fun observeFavoriteFoods(): Flow<List<ProfileFavoriteFoodEntity>>

    @Query("SELECT * FROM AccountSettings") abstract fun observeSettings(): Flow<SettingsEntity?>

    @Upsert protected abstract suspend fun upsertProfiles(profileEntity: List<ProfileEntity>)

    @Upsert protected abstract suspend fun upsertSettings(settingsEntity: SettingsEntity)

    @Query("DELETE FROM ProfileFavoriteFood") protected abstract suspend fun deleteFavoriteFoods()

    @Insert
    protected abstract suspend fun insertFavoriteFoods(
        favoriteFoods: List<ProfileFavoriteFoodEntity>
    )

    @Transaction
    open suspend fun upsertAccountWithDetails(
        profiles: List<ProfileEntity>,
        favoriteFoods: List<ProfileFavoriteFoodEntity>,
        settings: SettingsEntity,
    ) {
        upsertProfiles(profiles)
        deleteFavoriteFoods()
        insertFavoriteFoods(favoriteFoods)
        upsertSettings(settings)
    }
}
