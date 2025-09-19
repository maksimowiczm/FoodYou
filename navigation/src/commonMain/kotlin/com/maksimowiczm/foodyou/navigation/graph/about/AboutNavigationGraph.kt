package com.maksimowiczm.foodyou.navigation.graph.about

import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.maksimowiczm.foodyou.app.ui.about.opensource.AboutScreen
import com.maksimowiczm.foodyou.app.ui.sponsor.SponsorScreen
import com.maksimowiczm.foodyou.app.ui.sponsor.SponsorshipMethodsScreen
import com.maksimowiczm.foodyou.navigation.domain.AboutDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutMasterDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorDestination
import com.maksimowiczm.foodyou.navigation.domain.AboutSponsorMessagesDestination
import com.maksimowiczm.foodyou.shared.compose.navigation.forwardBackwardComposable

internal fun NavGraphBuilder.aboutNavigationGraph(
    masterOnBack: () -> Unit,
    masterOnSponsor: () -> Unit,
    sponsorOnBack: () -> Unit,
    sponsorOnSponsorshipMethods: () -> Unit,
    sponsorshipMethodOnBack: () -> Unit,
) {
    navigation<AboutDestination>(startDestination = AboutMasterDestination) {
        forwardBackwardComposable<AboutMasterDestination> {
            AboutScreen(onBack = masterOnBack, onSponsor = masterOnSponsor)
        }
        forwardBackwardComposable<AboutSponsorMessagesDestination> {
            SponsorScreen(
                onBack = sponsorOnBack,
                onSponsorshipMethods = sponsorOnSponsorshipMethods,
                animatedVisibilityScope = this,
            )
        }
        forwardBackwardComposable<AboutSponsorDestination> {
            SponsorshipMethodsScreen(onBack = sponsorshipMethodOnBack)
        }
    }
}
