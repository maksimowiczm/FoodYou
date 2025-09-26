package com.maksimowiczm.foodyou.sponsorship

import com.maksimowiczm.foodyou.sponsorship.domain.sponsorshipDomainModule
import com.maksimowiczm.foodyou.sponsorship.infrastructure.sponsorshipInfrastructureModule
import org.koin.dsl.module

val sponsorshipModule = module {
    sponsorshipDomainModule()
    sponsorshipInfrastructureModule()
}
