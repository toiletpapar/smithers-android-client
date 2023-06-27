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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.budgeting_client.R
import com.example.budgeting_client.models.crawler.CrawlerErrors
import com.example.budgeting_client.models.crawler.CrawlerTypes
import com.example.budgeting_client.models.crawler.CreateCrawlerPayload
import com.example.budgeting_client.ui.navigation.ContextItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaAdd(
    onClose: () -> Unit,
    onSaveComplete: () -> Unit,
    mangaAddViewModel: MangaAddViewModel = viewModel(factory = MangaAddViewModel.Factory)
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var crawler by rememberSaveable { mutableStateOf(CreateCrawlerPayload(name = "", url = "", adapter = CrawlerTypes.WEBTOON)) }
    val scope = rememberCoroutineScope()

    val errors = mangaAddViewModel.uiState.errors
    val isSaving = mangaAddViewModel.uiState.isSaving

    LaunchedEffect(mangaAddViewModel.uiState.hasUnknownError) {
        if (mangaAddViewModel.uiState.hasUnknownError) {
            snackbarHostState.showSnackbar(CrawlerErrors.UNKNOWN_ERROR.message)
        }
    }

    LaunchedEffect(mangaAddViewModel.uiState.isSaveComplete) {
        if (mangaAddViewModel.uiState.isSaveComplete) {
            onSaveComplete()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                    IconButton(
                        onClick = { scope.launch { mangaAddViewModel.saveCrawler(crawler) } },
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
                value = crawler.name,
                onValueChange = { crawler = crawler.copy(name = it) },
                label = { Text(stringResource(id = R.string.manga_title_form)) },
                singleLine = true,
                modifier = modifier,
                isError = errors?.hasOneOfError(listOf(CrawlerErrors.DUPLICATE_NAME_KEY, CrawlerErrors.EMPTY_NAME)) ?: false,
                supportingText = errors?.createErrorComposable(listOf(CrawlerErrors.DUPLICATE_NAME_KEY, CrawlerErrors.EMPTY_NAME))
            )

            TextField(
                value = crawler.url,
                onValueChange = { crawler = crawler.copy(url = it) },
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