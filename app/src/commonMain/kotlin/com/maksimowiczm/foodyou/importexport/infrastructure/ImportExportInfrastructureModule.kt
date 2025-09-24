package com.maksimowiczm.foodyou.importexport.infrastructure

import com.maksimowiczm.foodyou.importexport.domain.service.DatabaseDumpService
import org.koin.core.module.Module
import org.koin.dsl.bind

internal fun Module.importExportInfrastructureModule() {
    factory { RoomDatabaseDumpService(database = get()) }.bind<DatabaseDumpService>()
}
