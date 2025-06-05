package com.maksimowiczm.foodyou.feature.product.data.network.usda

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import com.maksimowiczm.foodyou.core.ext.getBlocking
import com.maksimowiczm.foodyou.feature.product.domain.RemoteProductMatcher
import io.ktor.client.HttpClient

internal class USDAFacade(
    private val httpClient: HttpClient,
    private val dataStore: DataStore<Preferences>,
    private val matcher: RemoteProductMatcher = usdaUrlMatcher
) : RemoteProductMatcher by matcher {

    fun extractId(url: String): String? = try {
        regex.find(url)?.groups?.get(1)?.value
    } catch (e: Exception) {
        Logger.w(TAG, e) { "Failed to extract ID from URL: $url" }
        null
    }

    fun createRequest(id: String) = USDAProductRequest(
        client = httpClient,
        id = id,
        apiKey = dataStore
            .getBlocking(USDAPreferences.apiKeyPreferenceKey)
            ?.takeIf { it.isNotEmpty() }
            ?: "DEMO_KEY"
    )

    private companion object {
        private const val TAG = "USDAFacade"
    }
}

private val regex by lazy {
    Regex(
        pattern = "(?:https://)?(?:www\\.)?(?:.+\\.)?fdc\\.nal\\.usda\\.gov/food-details/(\\d+)",
        options = setOf(RegexOption.IGNORE_CASE)
    )
}

private val usdaUrlMatcher = RemoteProductMatcher { regex.containsMatchIn(it) }
