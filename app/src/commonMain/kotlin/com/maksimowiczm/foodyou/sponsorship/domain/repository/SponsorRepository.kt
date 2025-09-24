package com.maksimowiczm.foodyou.sponsorship.domain.repository

import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.YearMonth

interface SponsorRepository {
    /**
     * Observes sponsorships for a given year and month.
     *
     * @param yearMonth The year and month for which to observe sponsorships.
     * @return A flow emitting lists of sponsorships for the specified year and month.
     */
    fun observeByYearMonth(yearMonth: YearMonth): Flow<List<Sponsorship>>

    /**
     * Requests a synchronization of sponsorship data.
     *
     * This function is typically used to fetch the latest sponsorship information from a remote
     * source and update the local data store accordingly.
     *
     * @param yearMonth The year and month for which to request synchronization.
     */
    suspend fun requestSync(yearMonth: YearMonth)
}
