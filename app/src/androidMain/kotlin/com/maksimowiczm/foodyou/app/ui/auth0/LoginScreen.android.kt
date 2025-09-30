package com.maksimowiczm.foodyou.app.ui.auth0

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.android.result.UserProfile
import com.maksimowiczm.foodyou.common.auth.Session
import com.maksimowiczm.foodyou.common.auth.SessionRepository
import kotlin.time.toKotlinInstant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal actual fun rememberAuth0OnLogin(
    onLoginSuccess: () -> Unit,
    onLoginError: (Throwable) -> Unit,
    onCancel: () -> Unit,
): () -> Unit {
    val context = LocalContext.current
    val auth0Config: Auth0Config = koinInject()
    val sessionRepository: SessionRepository = koinInject()
    val coroutineScope = rememberCoroutineScope()

    val account = Auth0.getInstance(auth0Config.clientId, auth0Config.domain)

    return {
        WebAuthProvider.login(account)
            .withScheme(auth0Config.scheme)
            .withScope("openid email read:current_user")
            .withAudience(auth0Config.audience)
            .start(
                context,
                object : Callback<Credentials, AuthenticationException> {
                    override fun onFailure(error: AuthenticationException) {
                        if (error.isCanceled) onCancel() else onLoginError(error)
                    }

                    override fun onSuccess(result: Credentials) {
                        coroutineScope.launch {
                            sessionRepository.saveSession(
                                result.toSession(AuthenticationAPIClient(account))
                            )
                            onLoginSuccess()
                        }
                    }
                },
            )
    }
}

private suspend fun Credentials.toSession(client: AuthenticationAPIClient): Session {
    val deferred = CompletableDeferred<UserProfile>()

    client
        .userInfo(accessToken)
        .start(
            object : Callback<UserProfile, AuthenticationException> {
                override fun onFailure(error: AuthenticationException) {
                    deferred.completeExceptionally(error)
                }

                override fun onSuccess(result: UserProfile) {
                    deferred.complete(result)
                }
            }
        )

    val profile = deferred.await()
    return Session(
        userId = profile.getId()!!,
        userEmail = profile.email!!,
        accessToken = accessToken,
        expiresAt = expiresAt.toInstant().toKotlinInstant(),
    )
}
