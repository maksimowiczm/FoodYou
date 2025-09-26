package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Context
import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.room.execSQL
import com.maksimowiczm.foodyou.app.infrastructure.room.DATABASE_NAME
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.common.infrastructure.room.immediateTransaction
import java.io.InputStream
import java.io.OutputStream

class DeveloperActivity : FoodYouAbstractActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { FoodYouTheme { DeveloperScreen() } }
    }
}

@Composable
private fun DeveloperScreen() {
    Scaffold(topBar = { TopAppBar(title = { Text("Developer options") }) }) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
        ) {
            SaveDatabaseListItem()
            ReplaceDatabaseListItem()
        }
    }
}

@Composable
private fun SaveDatabaseListItem() {
    ListItem(
        headlineContent = { Text("Save database") },
        supportingContent = { Text("Saves the database to a file") },
        modifier = Modifier.clickable {},
    )
}

@Composable
private fun ReplaceDatabaseListItem() {
    var showReplaceDatabaseDialog by rememberSaveable { mutableStateOf(false) }

    if (showReplaceDatabaseDialog) {
        // TODO
    }

    ListItem(
        headlineContent = { Text("Replace database") },
        supportingContent = { Text("Replaces the database with a file") },
        modifier = Modifier.clickable { showReplaceDatabaseDialog = true },
    )
}

private suspend fun Context.backupDatabase(database: FoodYouDatabase, outputStream: OutputStream) {
    database.immediateTransaction { execSQL("PRAGMA wal_checkpoint(FULL);") }

    database.close()

    // Copy the database from the app's database path to the output stream
    val dbFile = getDatabasePath(DATABASE_NAME)
    dbFile.inputStream().use { input -> input.copyTo(outputStream) }
}

private fun Context.replaceDatabase(database: FoodYouDatabase, stream: InputStream) {
    database.close()

    // Copy the database from the input stream to the app's database path
    val dbFile = getDatabasePath(DATABASE_NAME)

    // Create backup of existing database
    if (dbFile.exists()) {
        val backupFile = getDatabasePath("$DATABASE_NAME.bak")
        dbFile.copyTo(backupFile, overwrite = true)
    }

    // Delete existing database
    dbFile.delete()

    // Copy new database
    stream.use { input -> dbFile.outputStream().use { output -> input.copyTo(output) } }
}
