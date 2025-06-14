package com.maksimowiczm.foodyou.ui.donate

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import foodyou.app.generated.resources.*
import foodyou.app.generated.resources.Res
import org.jetbrains.compose.resources.painterResource

sealed interface DonateOption {
    val name: String

    @Composable
    fun Icon(modifier: Modifier = Modifier)

    companion object {
        val fiat = listOf<LinkDonateOption>(
            liberapay,
            kofi
        )

        val crypto = listOf<CryptoDonateOption>(
            bitcoin,
            monero
        )
    }
}

interface LinkDonateOption : DonateOption {
    val url: String
}

interface CryptoDonateOption : DonateOption {
    val address: String
}

private val bitcoin = object : CryptoDonateOption {
    override val name = "Bitcoin"
    override val address = "bc1qml4g4jwt6mqq2tsk9u7udhwysmjfknx68taln2"

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.bitcoin_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}

private val monero = object : CryptoDonateOption {
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

private val kofi = object : LinkDonateOption {
    override val name = "ko-fi.com/maksimowiczm"
    override val url = "https://ko-fi.com/maksimowiczm"

    @Composable
    override fun Icon(modifier: Modifier) {
        Image(
            painter = painterResource(Res.drawable.kofi_logo),
            contentDescription = null,
            modifier = modifier
        )
    }
}

private val liberapay = object : LinkDonateOption {
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
