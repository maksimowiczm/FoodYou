package com.maksimowiczm.foodyou.common.infrastructure.auth

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.maksimowiczm.foodyou.common.auth.Session
import com.maksimowiczm.foodyou.common.auth.SessionRepository
import com.maksimowiczm.foodyou.common.crypto.MasterCrypto
import com.maksimowiczm.foodyou.common.domain.date.DateProvider
import com.maksimowiczm.foodyou.common.log.Logger
import io.ktor.utils.io.core.toByteArray
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SafeSessionRepository(
    private val masterCrypto: MasterCrypto,
    private val dataStore: DataStore<Preferences>,
    private val dateProvider: DateProvider,
    private val logger: Logger,
) : SessionRepository {
    override suspend fun saveSession(session: Session) {
        val encrypted = masterCrypto.encryptSession(session)
        val json = Json.encodeToString(encrypted)

        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply { this[SessionKeys.sessionKey] = json }
        }
    }

    override fun observeSession(): Flow<Session?> =
        dataStore.data.map { prefs ->
            try {
                val json = prefs[SessionKeys.sessionKey] ?: return@map null
                val encrypted = Json.decodeFromString<EncryptedSession>(json)

                val expiresAt =
                    masterCrypto
                        .decrypt(encrypted.expiresAt)
                        .decodeToString()
                        .toLong()
                        .let(Instant::fromEpochSeconds)

                if (dateProvider.nowInstant() >= expiresAt) {
                    clearSession()
                    return@map null
                }

                return@map Session(
                    userId = masterCrypto.decrypt(encrypted.userId).decodeToString(),
                    userEmail = masterCrypto.decrypt(encrypted.userEmail).decodeToString(),
                    accessToken = masterCrypto.decrypt(encrypted.accessToken).decodeToString(),
                    expiresAt = expiresAt,
                )
            } catch (e: Exception) {
                logger.e("SafeSessionRepository", e) { "Failed to get session" }
                null
            }
        }

    override suspend fun clearSession() {
        dataStore.updateData { prefs ->
            prefs.toMutablePreferences().apply { remove(SessionKeys.sessionKey) }
        }
    }
}

@Serializable
private class EncryptedSession(
    val userId: ByteArray,
    val userEmail: ByteArray,
    val accessToken: ByteArray,
    val expiresAt: ByteArray,
)

private suspend fun MasterCrypto.encryptSession(session: Session): EncryptedSession {
    return EncryptedSession(
        userId = encrypt(session.userId.toByteArray()),
        userEmail = encrypt(session.userEmail.toByteArray()),
        accessToken = encrypt(session.accessToken.toByteArray()),
        expiresAt = encrypt(session.expiresAt.epochSeconds.toString().toByteArray()),
    )
}

private object SessionKeys {
    val sessionKey = stringPreferencesKey("session_key")
}
