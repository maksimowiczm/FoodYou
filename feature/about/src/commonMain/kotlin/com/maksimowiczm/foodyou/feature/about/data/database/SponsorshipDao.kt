package com.maksimowiczm.foodyou.feature.about.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SponsorshipDao {

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds DESC")
    fun pagedFromLatest(): PagingSource<Int, Sponsorship>

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds DESC LIMIT 1")
    suspend fun getLatestSponsorship(): Sponsorship?

    @Query("SELECT * FROM Sponsorship ORDER BY sponsorshipEpochSeconds ASC LIMIT 1")
    suspend fun getOldestSponsorship(): Sponsorship?

    @Upsert
    suspend fun upsert(sponsorship: List<Sponsorship>)
}
