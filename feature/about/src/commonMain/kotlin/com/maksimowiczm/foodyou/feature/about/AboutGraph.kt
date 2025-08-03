package com.maksimowiczm.foodyou.feature.about

import androidx.navigation.NavGraphBuilder
import com.maksimowiczm.foodyou.core.navigation.forwardBackwardComposable
import com.maksimowiczm.foodyou.feature.about.ui.AboutScreen
import com.maksimowiczm.foodyou.feature.about.ui.SponsorMessagesScreen
import com.maksimowiczm.foodyou.feature.about.ui.SponsorScreen
import kotlinx.serialization.Serializable

@Serializable
data object About

@Serializable
data object SponsorMessages

@Serializable
data object Sponsor

fun NavGraphBuilder.aboutGraph(
    aboutOnBack: () -> Unit,
    aboutOnSponsor: () -> Unit,
    sponsorMessagesOnBack: () -> Unit,
    sponsorMessagesOnSponsor: () -> Unit,
    sponsorOnBack: () -> Unit
) {
    forwardBackwardComposable<About> {
        AboutScreen(
            onBack = aboutOnBack,
            onSponsor = aboutOnSponsor
        )
    }
    forwardBackwardComposable<SponsorMessages> {
        SponsorMessagesScreen(
            onBack = sponsorMessagesOnBack,
            onSponsor = sponsorMessagesOnSponsor
        )
    }
    forwardBackwardComposable<Sponsor> {
        SponsorScreen(
            onBack = sponsorOnBack
        )
    }
}
