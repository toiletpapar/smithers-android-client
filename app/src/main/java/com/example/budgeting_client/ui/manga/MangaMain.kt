package com.example.budgeting_client.ui.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgeting_client.LocalSnackbarHostState
import com.example.budgeting_client.SmithersApplication
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.FavouriteStatus
import com.example.budgeting_client.models.ReadStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaMain(
    onDrawerOpen: () -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onEditClick: (crawlTargetId: Int) -> Unit,
    mangaMainViewModel: MangaMainViewModel = viewModel(factory = MangaMainViewModel.Factory),
) {
    val snackbarHostState = LocalSnackbarHostState.current
    val mangas = mangaMainViewModel.uiState.mangas

    LaunchedEffect(mangas) {
        if (mangas.isEmpty()) {
            mangaMainViewModel.getMangas()
        }
    }

    LaunchedEffect(mangaMainViewModel.uiState.hasUnknownError) {
        if (mangaMainViewModel.uiState.hasUnknownError) {
            snackbarHostState.showSnackbar(CrawlerErrors.UNKNOWN_ERROR.message)
        }
    }

    LaunchedEffect(mangaMainViewModel.uiState.ackSync) {
        if (mangaMainViewModel.uiState.ackSync) {
            // Keep this line until sync uses workers
            mangaMainViewModel.getMangas()

            // Give user feedback
            snackbarHostState.showSnackbar("Sync acknowledged")
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
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Manga"
                        )
                    }
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
                        val latestUpdate = manga.updates.getOrNull(0)
                        val context = LocalContext.current
                        MangaCard(
                            modifier = Modifier
                                .fillMaxWidth(0.9F)
                                .align(Alignment.Center),
                            title = manga.name,
                            imageUrl = SmithersApplication.url.toString() + "api/v1/crawl-targets/${manga.crawlTargetId}/cover",
                            imageSignature = manga.coverSignature,
                            chapter = latestUpdate?.chapter,
                            chapterName = latestUpdate?.chapterName,
                            lastUpdated = latestUpdate?.dateCreated,
                            isRead = latestUpdate?.isRead ?: false,
                            isFavourite = manga.favourite,
                            latestCrawlSuccess = manga.crawlSuccess,
                            onEditClick = { onEditClick(manga.crawlTargetId) },
                            onSyncClick = { mangaMainViewModel.syncManga(manga.crawlTargetId) },
                            onCardClick = {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(latestUpdate?.readAt ?: manga.url))
                                ContextCompat.startActivity(
                                    context,
                                    browserIntent,
                                    null
                                )
                                latestUpdate?.let { update ->
                                    mangaMainViewModel.updateReadStatus(update.mangaUpdateId, ReadStatus(true))
                                }
                            },
                            onReadClick = {
                                latestUpdate?.let { update ->
                                    mangaMainViewModel.updateReadStatus(update.mangaUpdateId, ReadStatus(!update.isRead))
                                }
                            },
                            onFavouriteClick = {
                                mangaMainViewModel.updateFavourite(manga.crawlTargetId, FavouriteStatus(!manga.favourite))
                            }
                        )
                    }
                }
            }
        }
    )
}