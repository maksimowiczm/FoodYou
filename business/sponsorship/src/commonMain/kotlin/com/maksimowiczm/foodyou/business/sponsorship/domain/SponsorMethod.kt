@file:Suppress("SpellCheckingInspection", "ClassName")

package com.maksimowiczm.foodyou.business.sponsorship.domain

/** Represents a method of sponsoring the project. */
sealed interface SponsorMethod

/** Available methods of sponsoring the project. The ones that user can actually use. */
sealed interface AvailableSponsorMethod : SponsorMethod {
    val name: String
    val primary: Boolean
        get() = false

    companion object {
        val fiat: List<LinkSponsorMethod>
            get() = listOf(Ko_Fi)

        val crypto: List<CryptoSponsorMethod>
            get() = listOf(Bitcoin, Monero, Ethereum, Solana, Litecoin, Zcash, Dash, Avalanche)
    }
}

/** Methods that involve a link to an external site. */
sealed interface LinkSponsorMethod : AvailableSponsorMethod {
    val url: String
}

/** Methods that involve cryptocurrency donations. These have an associated wallet address. */
sealed interface CryptoSponsorMethod : AvailableSponsorMethod {
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
    override val name = "ko-fi.com/maksimowiczm"
    override val url = "https://ko-fi.com/maksimowiczm/5"
    override val primary = true
}

data object PayPal : SponsorMethod

data object Ethereum : CryptoSponsorMethod {
    override val name = "Ethereum"
    override val address = "0x7C794aF78235504014cC5c987161b80a803ee514"
}

data object Solana : CryptoSponsorMethod {
    override val name = "Solana"
    override val address = "6kdSsE5xQBmiQ5DY5bqWJX8jK2fuWiGHx9YgeAvFU4gq"
}

data object Litecoin : CryptoSponsorMethod {
    override val name = "Litecoin"
    override val address = "ltc1qrjpk7p4nzzm86lrfue2kz4ln4l6fjreha9lrvw"
}

data object Zcash : CryptoSponsorMethod {
    override val name = "Zcash"
    override val address =
        "u1hd2wvlp3qwgj2p68cz2cl3zajyjcruz6hhxmhmlrwq53n3sz32xtngjrrg2phtgzwlam370w3yjuf37k797y3w8tc4mc5lhs7nedxq2yze8kk44xr2tmnlzej0dw3u5lry4alvftejlf2qtz3r38gxyyvg54adkvncn0w7dhelt98letfjh2x5sda8ay50cnsupeg4jzpjy22dnmtsu"
}

data object Dash : CryptoSponsorMethod {
    override val name = "Dash"
    override val address = "XqDN5Yowv5r9Wpduh1k5LtFkidmBYgSGkQ"
}

data object Avalanche : CryptoSponsorMethod {
    override val name = "Avalanche"
    override val address = "0x7C794aF78235504014cC5c987161b80a803ee514"
}
