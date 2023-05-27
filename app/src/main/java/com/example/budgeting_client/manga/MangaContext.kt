package com.example.budgeting_client.manga

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.Date
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.budgeting_client.crawler.Crawler
import com.example.budgeting_client.crawler.CrawlerService
import com.example.budgeting_client.crawler.crawlerService

suspend fun getCrawlers(): List<Crawler>? {
    try {
        val response = crawlerService.getCrawlers()

        return if (response.isSuccessful) {
            response.body()
        } else {
            // TODO: Handle server response with toast "server responded with unknown error (xxx)
            //  where xxx is the status code", navigate to manga screen

            null
        }
    } catch (e: Exception) {
        // TODO: Handle any exceptions with toast "unknown error occurred"
        Log.e("BUDGETING_ERROR", e.message ?: "Unable to getCrawlers")

        return null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaContext(onDrawerOpen: () -> Unit, onAddClick: () -> Unit) {
//    val snackbarHostState = remember { SnackbarHostState() }
    var crawlers by remember { mutableStateOf(emptyList<Crawler>()) }

    LaunchedEffect(Unit) {
        val result = getCrawlers()

        if (result != null) {
            crawlers = result
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Manga",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDrawerOpen) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Main Menu"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onAddClick) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Manga"
                        )
                    }
                }
            )
        },
        content = { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    key = { index -> crawlers[index].crawlTargetId ?: index },
                    count = crawlers.size
                ) {
                    val crawler = crawlers[it]

                    Box(Modifier.fillMaxWidth().height(160.dp)) {
                        MangaCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9F)
                                .align(Alignment.Center),
                            title = crawler.name,
                            chapter = 10,   // TODO: Use LatestMangaUpdate to get latest chapter
                            lastUpdated = Date(),   // TODO: Use LatestMangaUpdate to get the date of the latest chapter
                            urlString = crawler.url
                        )
                    }
                }
            }
        }
    )
}