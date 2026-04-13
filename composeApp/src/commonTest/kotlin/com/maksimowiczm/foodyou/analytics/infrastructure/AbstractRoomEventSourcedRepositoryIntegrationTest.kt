package com.maksimowiczm.foodyou.analytics.infrastructure

import androidx.room.useReaderConnection
import com.maksimowiczm.foodyou.analytics.domain.Analytics
import com.maksimowiczm.foodyou.analytics.domain.AnalyticsEvent
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase
import com.maksimowiczm.foodyou.app.infrastructure.room.EventStoreDatabase.Companion.buildDatabase
import com.maksimowiczm.foodyou.common.clock.staticClock
import com.maksimowiczm.foodyou.common.infrastructure.provideRoomDatabaseBuilder
import com.maksimowiczm.foodyou.common.infrastructure.room.eventstore.AbstractRoomEventSourcedRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest

class AbstractRoomEventSourcedRepositoryIntegrationTest {
    private lateinit var database: EventStoreDatabase
    private lateinit var repository: AbstractRoomEventSourcedRepository<Analytics, AnalyticsEvent>

    @BeforeTest
    fun setup() {
        database = provideRoomDatabaseBuilder<EventStoreDatabase>().buildDatabase()
        repository = AnalyticsRepositoryImpl(database.eventStoreDao)
    }

    @AfterTest
    fun tearDown() {
        if (this::database.isInitialized) {
            database.close()
        }
    }

    @Test
    fun load_emptyStream_returnsEmptyAggregate() = runTest {
        val aggregate = repository.load()

        assertEquals(0, aggregate.launchCount)
        assertEquals(null, aggregate.currentVersion)
    }

    @Test
    fun save_persistsEventsAndLoad_rehydratesAggregate() = runTest {
        val clock = staticClock(Instant.fromEpochSeconds(1_700_000_000))
        val analytics =
            Analytics.of().also { it.recordAppLaunch(versionName = "1.0.0", clock = clock) }

        repository.save(analytics)
        val loaded = repository.load()

        assertEquals(1, loaded.launchCount)
        assertEquals("1.0.0", loaded.currentVersion)
        assertEquals(3, countEventsForStream("AnalyticsEvent"))
    }

    @Test
    fun save_withReloadedAggregate_appendsOnlyNewEvents() = runTest {
        val initialClock = staticClock(Instant.fromEpochSeconds(1_700_000_000))
        repository.save(
            Analytics.of().also { it.recordAppLaunch(versionName = "1.0.0", clock = initialClock) }
        )

        val loaded = repository.load()
        val updateClock = staticClock(Instant.fromEpochSeconds(1_700_000_100))
        loaded.recordAppLaunch(versionName = "1.1.0", clock = updateClock)

        repository.save(loaded)
        val reloaded = repository.load()

        assertEquals(2, reloaded.launchCount)
        assertEquals("1.1.0", reloaded.currentVersion)
        assertEquals(5, countEventsForStream("AnalyticsEvent"))
    }

    private suspend fun countEventsForStream(stream: String): Int =
        database.useReaderConnection { connection ->
            connection.usePrepared("SELECT COUNT(*) FROM StoredEvent WHERE eventStream = ?") {
                it.bindText(1, stream)
                it.step()
                it.getInt(0)
            }
        }
}
