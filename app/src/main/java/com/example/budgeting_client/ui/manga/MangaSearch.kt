package com.example.budgeting_client.ui.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgeting_client.LocalSnackbarHostState
import com.example.budgeting_client.R
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.QueryCrawlerErrors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSearch(
    onBack: () -> Unit,
    onSearchResultClick: (payload: CreateCrawlerPayload) -> Unit,
    mangaSearchViewModel: MangaSearchViewModel = viewModel(factory = MangaSearchViewModel.Factory),
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = LocalSnackbarHostState.current

    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val dismissSheet: () -> Unit = {
        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
            if (!bottomSheetState.isVisible) {
                openBottomSheet = false
            }
        }
    }
    val search: (append: Boolean) -> Unit = { append ->
        mangaSearchViewModel.searchMangas(mangaSearchViewModel.uiState.searchPayload, append)
    }

    var dropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(mangaSearchViewModel.uiState.errors) {
        if (mangaSearchViewModel.uiState.errors?.hasOneOfError(listOf(QueryCrawlerErrors.EMPTY_QUERY)) == true) {
            snackbarHostState.showSnackbar(QueryCrawlerErrors.EMPTY_QUERY.message)
        } else if (mangaSearchViewModel.uiState.errors?.hasOneOfError(listOf(QueryCrawlerErrors.UNKNOWN_ERROR)) == true) {
            snackbarHostState.showSnackbar(QueryCrawlerErrors.UNKNOWN_ERROR.message)
        }
    }

    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState.canScrollForward, mangaSearchViewModel.uiState.canLoadMore) {
        if (!lazyListState.canScrollForward && mangaSearchViewModel.uiState.canLoadMore) {
            mangaSearchViewModel.incrementSearchPage()
            search(true)
        }
    }

    val context = LocalContext.current

    // TODO: Replace custom search with material 3 search when available
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    BasicTextField(
                        value = mangaSearchViewModel.uiState.searchPayload.query,
                        onValueChange = { mangaSearchViewModel.setSearchQuery(it) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                search(false)
                                scope.launch {
                                    lazyListState.scrollToItem(0)
                                }
                            }
                        ),
                        // TODO: Figure out how to use the color from the android background theme
                        textStyle = TextStyle(fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface),
                        decorationBox = { innerTextField ->
                            Box(
                                modifier = Modifier
                            ) {
                                if (mangaSearchViewModel.uiState.searchPayload.query.isEmpty()) {
                                    Text(
                                        text = "Search"
                                    )
                                }
                                innerTextField()
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { openBottomSheet = !openBottomSheet }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.filter),
                            contentDescription = "Advanced Filter"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = lazyListState
        ) {
            items(
                count = mangaSearchViewModel.uiState.mangas.size
            ) {
                val manga = mangaSearchViewModel.uiState.mangas[it]

                ListItem(
                    headlineContent = { Text(manga.name) },
                    supportingContent = {
                        Text(manga.url)
                    },
                    trailingContent = {
                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            IconButton(
                                onClick = {
                                    onSearchResultClick(
                                        CreateCrawlerPayload(
                                            name = manga.name,
                                            adapter = manga.adapter,
                                            url = manga.url
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = "Add Manga"
                                )
                            }
                            IconButton(
                                onClick = {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(manga.url))
                                    ContextCompat.startActivity(
                                        context,
                                        browserIntent,
                                        null
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.launch),
                                    contentDescription = "Navigate to manga"
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                )
            }
        }

        if (openBottomSheet) {
            val windowInsets = WindowInsets(0)

            ModalBottomSheet(
                onDismissRequest = { openBottomSheet = false },
                sheetState = bottomSheetState,
                windowInsets = windowInsets,
                modifier = Modifier.fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                    CrawlerTypesDropdown(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                        modifier = Modifier.padding(vertical = 12.dp),
                        currentAdapter = mangaSearchViewModel.uiState.searchPayload.source,
                        onDismissRequest = { dropdownExpanded = false },
                        onCrawlerTypeClick = {
                            mangaSearchViewModel.setSearchSource(it)
                            dropdownExpanded = false
                        }
                    )
                    Row(Modifier.fillMaxSize().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(
                            onClick = dismissSheet
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                dismissSheet()
                                search(false)
                                scope.launch {
                                    lazyListState.scrollToItem(0)
                                }
                            },
                        ) {
                            Text("Search")
                        }
                    }
                }
            }
        }
    }
}