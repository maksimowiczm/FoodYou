package com.maksimowiczm.foodyou.app.infrastructure.android

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Process
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.room.execSQL
import androidx.room.useWriterConnection
import com.maksimowiczm.foodyou.app.infrastructure.room.DATABASE_NAME
import com.maksimowiczm.foodyou.app.infrastructure.room.FoodYouDatabase
import com.maksimowiczm.foodyou.app.ui.common.component.ArrowBackIconButton
import com.maksimowiczm.foodyou.app.ui.theme.FoodYouTheme
import com.maksimowiczm.foodyou.common.compose.utility.LocalClipboardManager
import com.maksimowiczm.foodyou.common.extension.now
import foodyou.app.generated.resources.*
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

// This is not developer activity anymore but a database backup activity
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
private fun Activity.DeveloperScreen(onReplaced: () -> Unit) {
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
                Text(stringResource(Res.string.description_replacing_database))
            }
        }
    } else {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(Res.string.headline_database_backup)) },
                    navigationIcon = { ArrowBackIconButton(onClick = { finish() }) },
                    scrollBehavior = scrollBehavior,
                )
            }
        ) { paddingValues ->
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = paddingValues,
            ) {
                item {
                    val clipboard = LocalClipboardManager.current
                    val text =
                        "Backup and restore is experimental. Only food data and diary entries are saved. User settings and daily nutrient goals are not backed up."

                    Surface(
                        onClick = { clipboard.copy("text", text) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Science,
                                    contentDescription = null,
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = stringResource(Res.string.headline_experimental),
                                    style = MaterialTheme.typography.titleMedium,
                                )
                            }
                            // Don't translate it because if something changes in the future,
                            // translations may be misleading.
                            Text(text = text, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                item { SaveDatabaseListItem() }
                item {
                    ReplaceDatabaseListItem(
                        onLock = { isReplacingDatabase = true },
                        onFinish = onReplaced,
                    )
                }
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
        headlineContent = { Text(stringResource(Res.string.headline_save_database)) },
        supportingContent = { Text(stringResource(Res.string.description_save_database)) },
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
            title = { Text(stringResource(Res.string.headline_replace_database)) },
            text = {
                val str = stringResource(Res.string.warning_replace_database)
                val words = remember(str) { str.customSplit() }
                val annotated =
                    remember(typography.bodyLarge, words) {
                        buildAnnotatedString {
                            words.forEachIndexed { i, word ->
                                if (i > 0 && word.all(Char::isLetterOrDigit)) append(" ")

                                if (word.all(Char::isUpperCase) && word.length >= 2) {
                                    withStyle(
                                        typography.bodyLarge.toSpanStyle().copy(color = Color.Red)
                                    ) {
                                        append(word)
                                    }
                                } else {
                                    append(word)
                                }
                            }
                        }
                    }

                Text(text = annotated, style = typography.bodyMedium)
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
                                append(stringResource(Res.string.action_replace))
                                if (timeout > 0) {
                                    append(" ($timeout)")
                                }
                            }
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showReplaceDatabaseDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            },
        )
    }

    ListItem(
        headlineContent = { Text(stringResource(Res.string.headline_replace_database)) },
        supportingContent = { Text(stringResource(Res.string.description_replace_database)) },
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

/**
 * Custom string splitting
 *
 * Example:
 * ```kotlin
 * val text = "Hello,   world! This is a test."
 * val words = text.splitWords()
 * // words will be ["Hello", ",", "world", "!", "This", "is", "a", "test", "."]
 */
private fun String.customSplit(): List<String> {
    val words = mutableListOf<String>()
    val currentWord = StringBuilder()

    for (char in this) {
        if (char.isWhitespace()) {
            if (currentWord.isNotEmpty()) {
                words.add(currentWord.toString())
                currentWord.clear()
            }
        } else if (char.isLetterOrDigit()) {
            currentWord.append(char)
        } else {
            if (currentWord.isNotEmpty()) {
                words.add(currentWord.toString())
                currentWord.clear()
            }
            words.add(char.toString())
        }
    }

    if (currentWord.isNotEmpty()) {
        words.add(currentWord.toString())
    }

    return words
}
