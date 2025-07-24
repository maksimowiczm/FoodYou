package com.maksimowiczm.foodyou.feature.about.ui

import androidx.compose.runtime.*
import com.maksimowiczm.foodyou.feature.about.data.database.Sponsorship
import com.maksimowiczm.foodyou.feature.about.data.database.SponsorshipMethod
import foodyou.app.generated.resources.*

val Sponsorship.iconResource
    @Composable get() = when (typedMethod) {
        null -> null // Unknown method, no icon available
        SponsorshipMethod.Kofi -> Res.drawable.kofi_logo
        SponsorshipMethod.Liberapay -> Res.drawable.liberapay_logo
        SponsorshipMethod.Crypto if (currency == "BTC") -> Res.drawable.bitcoin_logo
        SponsorshipMethod.Crypto if (currency == "XMR") -> Res.drawable.monero_logo
        SponsorshipMethod.Crypto -> null
        SponsorshipMethod.PayPal -> Res.drawable.paypal_logo
    }
