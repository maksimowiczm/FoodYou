package com.maksimowiczm.foodyou.app.infrastructure

import com.maksimowiczm.foodyou.app.BuildConfig
import com.maksimowiczm.foodyou.app.ui.auth0.Auth0Config

class FoodYouAuth0Config : Auth0Config {
    override val scheme: String = BuildConfig.AUTH0_SCHEME
    override val domain: String = BuildConfig.AUTH0_DOMAIN
    override val audience: String = "https://${BuildConfig.AUTH0_DOMAIN}/api/v2/"
    override val clientId: String = BuildConfig.AUTH0_CLIENT_ID
}
