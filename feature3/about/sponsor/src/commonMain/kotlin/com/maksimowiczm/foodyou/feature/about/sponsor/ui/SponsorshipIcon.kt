package com.maksimowiczm.foodyou.feature.about.sponsor.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.maksimowiczm.foodyou.business.sponsorship.domain.Bitcoin
import com.maksimowiczm.foodyou.business.sponsorship.domain.Ko_Fi
import com.maksimowiczm.foodyou.business.sponsorship.domain.Liberapay
import com.maksimowiczm.foodyou.business.sponsorship.domain.Monero
import com.maksimowiczm.foodyou.business.sponsorship.domain.SponsorMethod
import com.maksimowiczm.foodyou.business.sponsorship.domain.Sponsorship
import foodyou.app.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun SponsorMethod.Icon(modifier: Modifier = Modifier) {
    when (this) {
        Bitcoin ->
            Image(
                painter = painterResource(Res.drawable.bitcoin_logo),
                contentDescription = null,
                modifier = modifier,
            )

        Monero ->
            Image(
                painter = painterResource(Res.drawable.monero_logo),
                contentDescription = null,
                modifier = modifier,
            )

        Ko_Fi ->
            Image(
                painter = painterResource(Res.drawable.kofi_logo),
                contentDescription = null,
                modifier = modifier,
            )

        Liberapay ->
            Image(
                painter = painterResource(Res.drawable.liberapay_logo),
                contentDescription = null,
                modifier = modifier,
            )
    }
}

val Sponsorship.iconResource
    @Composable
    get() =
        when (this.method) {
            "Ko-fi" -> Res.drawable.kofi_logo
            "Liberapay" -> Res.drawable.liberapay_logo
            "Crypto" if (currency == "BTC") -> Res.drawable.bitcoin_logo
            "Crypto" if (currency == "XMR") -> Res.drawable.monero_logo
            "PayPal" -> Res.drawable.paypal_logo
            else -> null
        }
