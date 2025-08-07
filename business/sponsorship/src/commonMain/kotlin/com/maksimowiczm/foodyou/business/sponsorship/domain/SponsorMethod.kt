@file:Suppress("SpellCheckingInspection", "ClassName")

package com.maksimowiczm.foodyou.business.sponsorship.domain

sealed interface SponsorMethod {
    val name: String
    val primary: Boolean
        get() = false

    companion object {
        val fiat = listOf<LinkSponsorMethod>(Ko_Fi, Liberapay)

        val crypto = listOf<CryptoSponsorMethod>(Bitcoin, Monero)
    }
}

sealed interface LinkSponsorMethod : SponsorMethod {
    val url: String
}

sealed interface CryptoSponsorMethod : SponsorMethod {
    val address: String
}

data object Bitcoin : CryptoSponsorMethod {
    override val name = "Bitcoin"
    override val address = "bc1qml4g4jwt6mqq2tsk9u7udhwysmjfknx68taln2"
    override val primary = true
}

data object Monero : CryptoSponsorMethod {
    override val name = "Monero"
    override val address =
        "41tP8QxdL5hduxcntGwJD92GJDdCTKDyyGSKofbgdgaLG2uJuqgK7daYymBQuJ1iA48LuiLdfoduFMLk1kdkTRKSC4mHkMY"
}

data object Ko_Fi : LinkSponsorMethod {
    override val name = "Ko-Fi"
    override val url = "https://ko-fi.com/maksimowiczm"
    override val primary = true
}

data object Liberapay : LinkSponsorMethod {
    override val name = "Liberapay"
    override val url = "https://liberapay.com/maksimowiczm"
}
