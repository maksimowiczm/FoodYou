@file:Suppress("SpellCheckingInspection", "ClassName")

package com.maksimowiczm.foodyou.sponsorship.domain.entity

/** Represents a method of sponsoring the project. */
sealed interface SponsorMethod

/** Available methods of sponsoring the project. The ones that user can actually use. */
sealed interface AvailableSponsorMethod : SponsorMethod {
    val name: String

    companion object {
        val fiat: List<LinkSponsorMethod>
            get() = listOf(Ko_Fi)

        val crypto: List<CryptoSponsorMethod>
            get() = listOf(Bitcoin, Monero)
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
}

data object Monero : CryptoSponsorMethod {
    override val name = "Monero"
    override val address =
        "41eXqs6zg8PFQ8Fec3iyYcVA3rFHc7wgj9hLRuiVh2FtbE2q2TGoCbhSmVX5R76SmYPpSM2VR7qmD4SQ4YMZCEFK6DGGWfB"
}

data object Ko_Fi : LinkSponsorMethod {
    override val name = "ko-fi.com/maksimowiczm"
    override val url = "https://ko-fi.com/maksimowiczm/5"
}

data object PayPal : SponsorMethod

data object Dash : SponsorMethod
