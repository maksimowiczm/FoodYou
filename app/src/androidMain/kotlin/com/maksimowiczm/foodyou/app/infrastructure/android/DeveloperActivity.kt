package com.maksimowiczm.foodyou.app.infrastructure.android

import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.room.execSQL
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.app.infrastructure.room.DATABASE_NAME
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.common.extension.now
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.koin.compose.koinInject

class DeveloperActivity : FoodYouAbstractActivity() {
    private val processId = Process.myPid()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodYouTheme {
                DeveloperScreen(
                    onReplaced = {
                        finish()
                        Process.killProcess(processId)
                    }
                )
            }
        }
    }
}

@Composable
private fun Context.DeveloperScreen(onReplaced: () -> Unit) {
    var isReplacingDatabase by rememberSaveable { mutableStateOf(false) }

    if (isReplacingDatabase) {
        Scaffold { paddingValues ->
            Column(
                modifier =
                    Modifier.padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .safeContentPadding()
                        .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularWavyProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text("Replacing database, please wait...")
            }
        }
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Developer options") }) }) { paddingValues ->
            Column(
                modifier =
                    Modifier.padding(paddingValues)
                        .consumeWindowInsets(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
            ) {
                SaveDatabaseListItem()
                ReplaceDatabaseListItem(
                    onLock = { isReplacingDatabase = true },
                    onFinish = onReplaced,
                )
            }
        }
    }
}

@Composable
private fun Context.SaveDatabaseListItem() {
    val database: FoodYouDatabase = koinInject()

    val launcher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/vnd.sqlite3")
        ) { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            } else {
                val stream = contentResolver.openOutputStream(uri)

                if (stream == null) {
                    return@rememberLauncherForActivityResult
                }

                stream.use { backupDatabase(database, it) }
            }
        }

    ListItem(
        headlineContent = { Text("Save database") },
        supportingContent = { Text("Saves the database to a file") },
        modifier =
            Modifier.clickable {
                val date = LocalDateTime.now()
                launcher.launch("foodyou-database-$date.db")
            },
    )
}

@Composable
private fun Context.ReplaceDatabaseListItem(onLock: () -> Unit, onFinish: () -> Unit) {
    var showReplaceDatabaseDialog by rememberSaveable { mutableStateOf(false) }
    val database: FoodYouDatabase = koinInject()
    val typography = MaterialTheme.typography

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri == null) {
                return@rememberLauncherForActivityResult
            } else {
                val stream = contentResolver.openInputStream(uri)

                if (stream == null) {
                    return@rememberLauncherForActivityResult
                }

                onLock()
                stream.use { replaceDatabase(database, it) }
                onFinish()
            }
        }

    if (showReplaceDatabaseDialog) {
        var timeout by rememberSaveable { mutableIntStateOf(5) }
        LaunchedEffect(Unit) {
            while (timeout > 0) {
                delay(1000)
                timeout--
            }
        }

        AlertDialog(
            onDismissRequest = { showReplaceDatabaseDialog = false },
            title = { Text("Replace database") },
            text = {
                Text(
                    buildAnnotatedString {
                        append("This will")
                        withStyle(typography.bodyLarge.toSpanStyle().copy(color = Color.Red)) {
                            append(" REPLACE ")
                        }
                        append("your existing database. This means that you will")
                        withStyle(typography.bodyLarge.toSpanStyle().copy(color = Color.Red)) {
                            append(" LOSE ")
                        }
                        append(
                            "your existing data. App will restart after replacing the database. You might need to open the app again."
                        )
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        launcher.launch(arrayOf("application/vnd.sqlite3", "*/*"))
                        showReplaceDatabaseDialog = false
                    },
                    enabled = timeout == 0,
                ) {
                    Text(
                        text =
                            buildString {
                                append("Replace")
                                if (timeout > 0) {
                                    append(" ($timeout)")
                                }
                            }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplaceDatabaseDialog = false }) { Text("Cancel") }
            },
        )
    }

    ListItem(
        headlineContent = { Text("Replace database") },
        supportingContent = { Text("Replaces the database with a file") },
        modifier = Modifier.clickable { showReplaceDatabaseDialog = true },
    )
}

private fun Context.backupDatabase(database: FoodYouDatabase, outputStream: OutputStream) {
    runBlocking(Dispatchers.IO) {
        database.useWriterConnection { it.execSQL("PRAGMA wal_checkpoint(FULL);") }
    }

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
