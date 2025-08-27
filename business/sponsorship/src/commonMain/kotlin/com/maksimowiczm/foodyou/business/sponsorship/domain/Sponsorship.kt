package com.maksimowiczm.foodyou.business.sponsorship.domain

import kotlinx.datetime.LocalDateTime

/**
 * A sponsorship made by a sponsor.
 *
 * @property id Unique identifier of the sponsorship.
 * @property sponsorName Name of the sponsor. Can be null if the sponsor chose to remain anonymous.
 * @property message Optional message from the sponsor. Can be null if no message was provided.
 * @property amount Amount of money sponsored, represented as a string to preserve formatting.
 * @property currency Currency code of the sponsored amount (e.g., "USD", "EUR").
 * @property inEuro Equivalent amount in Euros, represented as a string for consistency.
 * @property dateTime Date and time when the sponsorship was made.
 * @property method Method used for the sponsorship (e.g., "PayPal", "Ko-fi").
 */
data class Sponsorship(
    val id: Long,
    val sponsorName: String?,
    val message: String?,
    val amount: String,
    val currency: String,
    val inEuro: String,
    val dateTime: LocalDateTime,
    val method: String,
)
