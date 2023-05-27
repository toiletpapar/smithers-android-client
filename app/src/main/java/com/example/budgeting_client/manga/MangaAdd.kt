package com.example.budgeting_client.manga

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.budgeting_client.R
import com.example.budgeting_client.crawler.Crawler
import com.example.budgeting_client.crawler.CrawlerTypes
import com.example.budgeting_client.crawler.crawlerService
import com.example.budgeting_client.navigation.ContextItem
import kotlinx.coroutines.launch


suspend fun onSave(crawler: Crawler, onAddComplete: () -> Unit) {
    try {
        val response = crawlerService.createCrawler(crawler)

        if (response.isSuccessful) {
            // TODO: Display toast showing success
            // Composing the parent context refreshes the list
            onAddComplete()
        } else {
            // TODO: Handle server response with toast "server responded with unknown error (xxx) where xxx is the status code", navigate to manga screen
            Log.i("BUDGETING_INFO", response.message())
            onAddComplete()
        }
    } catch (e: Exception) {
        // TODO: Handle any exceptions with toast "unknown error occurred"
        Log.e("BUDGETING_ERROR", e.message ?: "Unable to getCrawlers")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaAdd(onClose: () -> Unit, onAddComplete: () -> Unit) {
    var crawler by rememberSaveable { mutableStateOf(Crawler(name = "", url = "", adapter = CrawlerTypes.WEBTOON)) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = ContextItem.MangaAdd.title),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { scope.launch { onSave(crawler, onAddComplete) } }) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val modifier = Modifier.fillMaxWidth(0.8f)

            TextField(
                value = crawler.name,
                onValueChange = { crawler = crawler.copy(name = it) },
                label = { Text(stringResource(id = R.string.manga_title_form)) },
                singleLine = true,
                modifier = modifier
            )

            TextField(
                value = crawler.url,
                onValueChange = { crawler = crawler.copy(url = it) },
                label = { Text(stringResource(id = R.string.manga_url_form)) },
                singleLine = true,
                modifier = modifier
            )

            var expanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    // The `menuAnchor` modifier must be passed to the text field for correctness.
                    modifier = Modifier.menuAnchor().then(modifier),
                    readOnly = true,
                    value = crawler.adapter.displayName,
                    onValueChange = {},
                    label = { Text(stringResource(R.string.manga_adapter_form)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    CrawlerTypes.values().forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.displayName) },
                            onClick = {
                                crawler = crawler.copy(adapter = option)
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }
        }
    }
}