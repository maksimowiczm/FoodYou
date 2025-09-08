package com.maksimowiczm.foodyou.sponsorship.domain.repository

import androidx.paging.PagingData
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import kotlinx.coroutines.flow.Flow

interface SponsorRepository {
    fun observeSponsorships(fetchRemote: Boolean? = null): Flow<PagingData<Sponsorship>>
}
