package com.example.budgeting_client.ui.manga

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.budgeting_client.SmithersApplication
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CrawlerTypes
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.UpdateCrawlerPayload
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.utils.AppErrors
import kotlinx.coroutines.launch

// initial state
data class MangaUpsertUiState (
    val isSaveComplete: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val hasUnknownError: Boolean = false,
    val errors: AppErrors<CrawlerErrors>? = null,
    val crawler: CreateCrawlerPayload = CreateCrawlerPayload(name = "", url = "", adapter = CrawlerTypes.WEBTOON)
)

// reduce
class MangaUpsertViewModel constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    var uiState by mutableStateOf(MangaUpsertUiState())
        private set

    fun setCrawlerName(name: String) {
        val newCrawler = uiState.crawler.clone()
        newCrawler.name = name

        uiState = uiState.copy(
            crawler = newCrawler
        )
    }

    fun setCrawlerUrl(url: String) {
        val newCrawler = uiState.crawler.clone()
        newCrawler.url = url

        uiState = uiState.copy(
            crawler = newCrawler
        )
    }

    fun setCrawlerAdapter(adapter: CrawlerTypes) {
        val newCrawler = uiState.crawler.clone()
        newCrawler.adapter = adapter

        uiState = uiState.copy(
            crawler = newCrawler
        )
    }

    fun getCrawler(crawlTargetId: Int) {
        uiState = uiState.copy(
            isLoading = true,
        )

        viewModelScope.launch {
            try {
                val response = mangaRepository.getCrawler(crawlTargetId)

                uiState = if (!response.isSuccessful || response.value == null) {
                    uiState.copy(
                        errors = response.errors,
                        isLoading = false,
                        hasUnknownError = response.errors?.hasOneOfError(listOf(CrawlerErrors.UNKNOWN_ERROR)) ?: false
                    )
                } else {
                    uiState.copy(
                        errors = null,
                        hasUnknownError = false,
                        isLoading = false,
                        crawler = CreateCrawlerPayload(name = response.value.name, url = response.value.url, adapter = response.value.adapter)
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to save crawlers.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    hasUnknownError = true,
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                )
            }
        }
    }

    fun saveCrawler(crawler: CreateCrawlerPayload) {
        uiState = uiState.copy(
            isSaving = true,
        )

        viewModelScope.launch {
            try {
                val response = mangaRepository.saveCrawler(crawler)

                uiState = if (!response.isSuccessful) {
                    uiState.copy(
                        errors = response.errors,
                        isSaving = false,
                        hasUnknownError = response.errors?.hasOneOfError(listOf(CrawlerErrors.UNKNOWN_ERROR)) ?: false
                    )
                } else {
                    uiState.copy(
                        errors = null,
                        hasUnknownError = false,
                        isSaving = false,
                        isSaveComplete = true
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to save crawlers.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    hasUnknownError = true,
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                )
            }
        }
    }

    fun updateCrawler(crawlTargetId: Int, crawler: CreateCrawlerPayload) {
        uiState = uiState.copy(
            isSaving = true,
        )

        viewModelScope.launch {
            try {
                val response = mangaRepository.updateCrawler(crawlTargetId = crawlTargetId, crawlerUpdate = UpdateCrawlerPayload(data = crawler))

                uiState = if (!response.isSuccessful) {
                    uiState.copy(
                        errors = response.errors,
                        isSaving = false,
                        hasUnknownError = response.errors?.hasOneOfError(listOf(CrawlerErrors.UNKNOWN_ERROR)) ?: false
                    )
                } else {
                    uiState.copy(
                        errors = null,
                        hasUnknownError = false,
                        isSaving = false,
                        isSaveComplete = true
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to update crawlers.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    hasUnknownError = true,
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                // Get the Application object from extras
                val application = checkNotNull(extras[APPLICATION_KEY])

                return (application as SmithersApplication).mangaRepository?.let {
                    MangaUpsertViewModel(
                        it
                    )
                } as T
            }
        }
    }
}
