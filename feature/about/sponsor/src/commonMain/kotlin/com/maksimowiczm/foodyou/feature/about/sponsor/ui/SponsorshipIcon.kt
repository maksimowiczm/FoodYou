package com.maksimowiczm.foodyou.feature.about.sponsor.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Avalanche
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Bitcoin
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Dash
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Ethereum
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Ko_Fi
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Litecoin
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Monero
import com.maksimowiczm.foodyou.sponsorship.domain.entity.PayPal
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Solana
import com.maksimowiczm.foodyou.sponsorship.domain.entity.SponsorMethod
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Sponsorship
import com.maksimowiczm.foodyou.sponsorship.domain.entity.Zcash
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

        Avalanche ->
            Image(
                painter = painterResource(Res.drawable.avalanche_token),
                contentDescription = null,
                modifier = modifier,
            )

        Dash ->
            Image(
                painter = painterResource(Res.drawable.dash_coin),
                contentDescription = null,
                modifier = modifier,
            )

        Ethereum ->
            Image(
                painter = painterResource(Res.drawable.eth_diamond_purple_purple),
                contentDescription = null,
                modifier = modifier.clip(CircleShape),
            )

        Litecoin ->
            Image(
                painter = painterResource(Res.drawable.litecoin_ltc_logo),
                contentDescription = null,
                modifier = modifier,
            )

        Solana ->
            Image(
                painter = painterResource(Res.drawable.solana_logomark),
                contentDescription = null,
                modifier = modifier,
            )

        Zcash ->
            Image(
                painter = painterResource(Res.drawable.zcash_icon),
                contentDescription = null,
                modifier = modifier,
            )

        PayPal ->
            Image(
                painter = painterResource(Res.drawable.paypal_logo),
                contentDescription = null,
                modifier = modifier,
            )
    }
}

fun Sponsorship.icon(): (@Composable (Modifier) -> Unit)? {
    val method =
        when (method) {
            "Ko-fi" -> Ko_Fi
            "Crypto" ->
                when (currency) {
                    "BTC" -> Bitcoin
                    "XMR" -> Monero
                    "ETH" -> Ethereum
                    "SOL" -> Solana
                    "LTC" -> Litecoin
                    "AVAX" -> Avalanche
                    "DASH" -> Dash
                    "ZEC" -> Zcash
                    else -> return null
                }

            "PayPal" -> PayPal
            else -> return null
        }

    return { modifier -> method.Icon(modifier) }
}
