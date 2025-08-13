package com.maksimowiczm.foodyou.infrastructure.di

import com.maksimowiczm.foodyou.feature.about.sponsor.presentation.SponsorMessagesViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val featureAboutSponsorModule = module { viewModelOf(::SponsorMessagesViewModel) }
