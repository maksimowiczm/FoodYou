package com.maksimowiczm.foodyou.business.shared.infrastructure.room.sponsorship

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SponsorshipDao {

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds DESC")
    fun pagedFromLatest(): PagingSource<Int, SponsorshipEntity>

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds DESC LIMIT 1")
    suspend fun getLatestSponsorship(): SponsorshipEntity?

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds ASC LIMIT 1")
    suspend fun getOldestSponsorship(): SponsorshipEntity?

    @Upsert suspend fun upsert(sponsorship: List<SponsorshipEntity>)
}
