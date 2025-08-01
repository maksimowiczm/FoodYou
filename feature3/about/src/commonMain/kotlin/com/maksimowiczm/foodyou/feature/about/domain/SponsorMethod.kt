package com.maksimowiczm.foodyou.feature.about.domain

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

internal sealed interface SponsorMethod {
    val name: String
    val primary: Boolean
        get() = false

    @Composable
    fun Icon(modifier: Modifier = Modifier)

    companion object {
        val fiat = listOf<LinkSponsorMethod>(
            kofi,
            liberapay
        )

        val crypto = listOf<CryptoSponsorMethod>(
            bitcoin,
            monero
        )
    }
}

internal interface LinkSponsorMethod : SponsorMethod {
    val url: String
}

internal interface CryptoSponsorMethod : SponsorMethod {
    val address: String
}

private val bitcoin = object : CryptoSponsorMethod {
    override val name = "Bitcoin"
    override val address = "bc1qml4g4jwt6mqq2tsk9u7udhwysmjfknx68taln2"
    override val primary = true

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.bitcoin_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}

private val monero = object : CryptoSponsorMethod {
    override val name = "Monero"
    override val address =
        "41tP8QxdL5hduxcntGwJD92GJDdCTKDyyGSKofbgdgaLG2uJuqgK7daYymBQuJ1iA48LuiLdfoduFMLk1kdkTRKSC4mHkMY"

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.monero_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}

private val kofi = object : LinkSponsorMethod {
    override val name = "ko-fi.com/maksimowiczm"
    override val url = "https://ko-fi.com/maksimowiczm/5"
    override val primary = true

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.kofi_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}

private val liberapay = object : LinkSponsorMethod {
    override val name = "liberapay.com/maksimowiczm"
    override val url = "https://liberapay.com/maksimowiczm"

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.liberapay_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}
