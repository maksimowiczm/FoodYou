package com.maksimowiczm.foodyou.sponsorship.infrastructure.room

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SponsorshipDao {
    @Query(
        """
        SELECT * FROM Sponsorship 
        WHERE sponsorshipEpochSeconds BETWEEN :fromEpochSeconds AND :toEpochSeconds
        """
    )
    fun observeDateBetween(
        fromEpochSeconds: Long,
        toEpochSeconds: Long,
    ): Flow<List<SponsorshipEntity>>

    @Upsert suspend fun upsert(sponsorship: List<SponsorshipEntity>)

    @Query("DELETE FROM Sponsorship") suspend fun deleteAll()
}
