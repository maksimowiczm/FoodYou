package com.maksimowiczm.foodyou.common.infrastructure.csv

import com.maksimowiczm.foodyou.common.csv.CsvParser
import com.maksimowiczm.foodyou.common.csv.RfcCsvParserTest

class CsvParserImplTest : RfcCsvParserTest() {
    override val parser: CsvParser = CsvParserImpl()
}
