package com.example.budgeting_client.ui.manga

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgeting_client.R
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CrawlerTypes
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.Manga
import com.example.budgeting_client.ui.navigation.SkeletonLoader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaUpsert(
    onClose: () -> Unit,
    onSaveComplete: () -> Unit,
    crawlTargetId: Int?,
    mangaUpsertViewModel: MangaUpsertViewModel = viewModel(factory = MangaUpsertViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val errors = mangaUpsertViewModel.uiState.errors
    val isSaving = mangaUpsertViewModel.uiState.isSaving

    LaunchedEffect(Unit) {
        if (crawlTargetId != null && crawlTargetId != -1) {
            mangaUpsertViewModel.getCrawler(crawlTargetId)
        }
    }

    LaunchedEffect(mangaUpsertViewModel.uiState.hasUnknownError) {
        if (mangaUpsertViewModel.uiState.hasUnknownError) {
            snackbarHostState.showSnackbar(CrawlerErrors.UNKNOWN_ERROR.message)
        }
    }

    LaunchedEffect(mangaUpsertViewModel.uiState.isSaveComplete) {
        if (mangaUpsertViewModel.uiState.isSaveComplete) {
            onSaveComplete()
        }
    }

    if (!mangaUpsertViewModel.uiState.isLoading) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(id = if (crawlTargetId == -1 || crawlTargetId == null) R.string.manga_add_title else R.string.manga_edit_title),
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
                        IconButton(
                            onClick = { scope.launch {
                                if (crawlTargetId == -1 || crawlTargetId == null)
                                    mangaUpsertViewModel.saveCrawler(mangaUpsertViewModel.uiState.crawler)
                                else
                                    mangaUpsertViewModel.updateCrawler(crawlTargetId, mangaUpsertViewModel.uiState.crawler)
                            } },
                            enabled = !isSaving // TODO: Queue requests with offline first
                        ) {
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
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val modifier = Modifier.fillMaxWidth(0.8f)

                TextField(
                    value = mangaUpsertViewModel.uiState.crawler.name,
                    onValueChange = { mangaUpsertViewModel.setCrawlerName(it) },
                    label = { Text(stringResource(id = R.string.manga_title_form)) },
                    singleLine = true,
                    modifier = modifier,
                    isError = errors?.hasOneOfError(listOf(CrawlerErrors.DUPLICATE_NAME_KEY, CrawlerErrors.EMPTY_NAME)) ?: false,
                    supportingText = errors?.createErrorComposable(listOf(CrawlerErrors.DUPLICATE_NAME_KEY, CrawlerErrors.EMPTY_NAME))
                )

                TextField(
                    value = mangaUpsertViewModel.uiState.crawler.url,
                    onValueChange = { mangaUpsertViewModel.setCrawlerUrl(it) },
                    label = { Text(stringResource(id = R.string.manga_url_form)) },
                    singleLine = true,
                    modifier = modifier,
                    isError = errors?.hasOneOfError(listOf(CrawlerErrors.EMPTY_URL, CrawlerErrors.INVALID_URL)) ?: false,
                    supportingText = errors?.createErrorComposable(listOf(CrawlerErrors.EMPTY_URL, CrawlerErrors.INVALID_URL))
                )

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    TextField(
                        // The `menuAnchor` modifier must be passed to the text field for correctness.
                        modifier = Modifier
                            .menuAnchor()
                            .then(modifier),
                        readOnly = true,
                        value = mangaUpsertViewModel.uiState.crawler.adapter.displayName,
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
                                    mangaUpsertViewModel.setCrawlerAdapter(option)
                                    expanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                            )
                        }
                    }
                }
            }
        }
    } else {
        SkeletonLoader()
    }
}