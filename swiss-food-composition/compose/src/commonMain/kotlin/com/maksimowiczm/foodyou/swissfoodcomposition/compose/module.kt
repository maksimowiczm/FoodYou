package com.maksimowiczm.foodyou.swissfoodcomposition.compose

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.maksimowiczm.foodyou.swissfoodcomposition.domain.SwissFoodCompositionRepository
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.SwissFoodCompositionRepositoryImpl
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase
import com.maksimowiczm.foodyou.swissfoodcomposition.infrastructure.room.SwissFoodCompositionDatabase.Companion.buildDatabase
import foodyou.swissfoodcomposition.generated.resources.Res
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.databasesDir
import io.github.vinceglb.filekit.div
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.path
import io.github.vinceglb.filekit.readBytes
import io.github.vinceglb.filekit.write
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okio.ByteString.Companion.toByteString
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

private const val SWISS_FOOD_COMPOSITION_DATABASE_NAME = "SwissFoodCompositionDatabase.db"

fun Module.swissFoodComposition() {
    single {
        val dbFile = FileKit.databasesDir / SWISS_FOOD_COMPOSITION_DATABASE_NAME
        runBlocking(Dispatchers.IO) {
            val bytes = Res.readBytes("files/$SWISS_FOOD_COMPOSITION_DATABASE_NAME")
            val resourceHash = bytes.sha256()
            val existingHash = if (dbFile.exists()) dbFile.readBytes().sha256() else null
            if (existingHash != resourceHash) dbFile.write(bytes)
        }
        Room.databaseBuilder<SwissFoodCompositionDatabase>(dbFile.path)
            .setDriver(BundledSQLiteDriver())
            .buildDatabase()
    }
    factory { get<SwissFoodCompositionDatabase>().swissFoodCompositionDao }

    factoryOf(::SwissFoodCompositionRepositoryImpl).bind<SwissFoodCompositionRepository>()
}

private fun ByteArray.sha256() = toByteString().sha256().hex()
