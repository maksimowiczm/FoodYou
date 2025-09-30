package com.maksimowiczm.foodyou.app.ui.auth0

// It's not really UI specific, but where else to put it? Maybe put everything in Auth0 package?
interface Auth0Config {
    val scheme: String
    val domain: String
    val audience: String
    val clientId: String
}
