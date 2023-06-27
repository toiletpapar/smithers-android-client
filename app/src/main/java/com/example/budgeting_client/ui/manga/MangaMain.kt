package com.example.budgeting_client.ui.manga

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgeting_client.models.CrawlerErrors
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaMain(
    onDrawerOpen: () -> Unit,
    onAddClick: () -> Unit,
    mangaMainViewModel: MangaMainViewModel = viewModel(factory = MangaMainViewModel.Factory),
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val mangas = mangaMainViewModel.uiState.mangas

    LaunchedEffect(Unit) {
        mangaMainViewModel.getMangas()
    }

    LaunchedEffect(mangaMainViewModel.uiState.hasUnknownError) {
        if (mangaMainViewModel.uiState.hasUnknownError) {
            snackbarHostState.showSnackbar(CrawlerErrors.UNKNOWN_ERROR.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    key = { index -> mangas[index].crawlTargetId },
                    count = mangas.size
                ) {
                    val manga = mangas[it]

                    Box(Modifier.fillMaxWidth().height(160.dp)) {
                        MangaCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9F)
                                .align(Alignment.Center),
                            title = manga.name,
                            chapter = 10,   // TODO: update.chapter
                            lastUpdated = Date(),   // TODO: update.crawledOn
                            urlString = "https://example.com", // TODO: update.readAt
                            isRead = true,  // TODO: update.isRead
                            lastRemoteSync = manga.lastCrawledOn
                        )
                    }
                }
            }
        }
    )
}