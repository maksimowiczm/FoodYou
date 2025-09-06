package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.sponsor.presentation.SponsorMessagesViewModel
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorshipPreferences
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val featureAboutSponsorModule = module {
    viewModel {
        SponsorMessagesViewModel(
            sponsorRepository = get(),
            preferencesRepository = get(named(SponsorshipPreferences::class.qualifiedName!!)),
        )
    }
}
