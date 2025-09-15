package com.maksimowiczm.foodyou.navigation.graph.about

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.app.ui.sponsor.SponsorMessagesScreen
import com.maksimowiczm.foodyou.app.ui.sponsor.SponsorScreen
import com.maksimowiczm.foodyou.feature.about.master.ui.AboutScreen
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.shared.compose.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.aboutNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnSponsor: () -> Unit,
    sponsorMessagesOnBack: () -> Unit,
    sponsorMessagesOnSponsor: () -> Unit,
    sponsorOnBack: () -> Unit,
) {
    navigation<AboutDestination>(startDestination = AboutMasterDestination) {
        forwardBackwardComposable<AboutMasterDestination> {
            AboutScreen(onBack = masterOnBack, onSponsor = masterOnSponsor)
        }
        forwardBackwardComposable<AboutSponsorMessagesDestination> {
            SponsorMessagesScreen(
                onBack = sponsorMessagesOnBack,
                onSponsor = sponsorMessagesOnSponsor,
            )
        }
        forwardBackwardComposable<AboutSponsorDestination> { SponsorScreen(onBack = sponsorOnBack) }
    }
}
