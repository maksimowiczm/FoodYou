package com.maksimowiczm.foodyou.app.ui.database.opensource

import com.maksimowiczm.foodyou.app.ui.database.opensource.databasedump.databaseDumpModule
import com.maksimowiczm.foodyou.app.ui.database.opensource.exportcsvproducts.exportCsvProductsModule
import com.maksimowiczm.foodyou.app.ui.database.opensource.externaldatabases.externalDatabasesModule
import com.maksimowiczm.foodyou.app.ui.database.opensource.importcsvproducts.importCsvProductsModule
import com.maksimowiczm.foodyou.app.ui.database.opensource.swissfoodcompositiondatabase.swissFoodCompositionDatabaseModule
import org.koin.dsl.module

val uiDatabaseModule = module {
    databaseDumpModule()
    exportCsvProductsModule()
    externalDatabasesModule()
    importCsvProductsModule()
    swissFoodCompositionDatabaseModule()
}
