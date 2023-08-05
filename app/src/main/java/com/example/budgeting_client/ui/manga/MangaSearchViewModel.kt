package com.example.budgeting_client.ui.manga

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.budgeting_client.SmithersApplication
import com.example.budgeting_client.models.Manga
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CrawlerTypes
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.FavouriteStatus
import com.example.budgeting_client.models.QueryCrawlerErrors
import com.example.budgeting_client.models.ReadStatus
import com.example.budgeting_client.models.SearchCrawlerPayload
import com.example.budgeting_client.models.UpdateCrawlerFavouritePayload
import com.example.budgeting_client.models.UpdateReadStatusPayload
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.utils.AppErrors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// initial state
data class MangaSearchUiState (
    val canLoadMore: Boolean = false,
    val mangas: List<CreateCrawlerPayload> = emptyList(),
    val errors: AppErrors<QueryCrawlerErrors>? = null,
    val searchPayload: SearchCrawlerPayload = SearchCrawlerPayload(query = "", page = 1, source = CrawlerTypes.WEBTOON)
)

// reduce
class MangaSearchViewModel constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    var uiState by mutableStateOf(MangaSearchUiState())
        private set

    fun setSearchQuery(query: String) {
        val newSearchPayload = uiState.searchPayload.clone()
        newSearchPayload.query = query
        newSearchPayload.page = 1

        uiState = uiState.copy(
            searchPayload = newSearchPayload
        )
    }

    fun incrementSearchPage() {
        val newSearchPayload = uiState.searchPayload.clone()
        newSearchPayload.page = (newSearchPayload.page + 1).toShort()

        uiState = uiState.copy(
            searchPayload = newSearchPayload
        )
    }

    fun setSearchSource(source: CrawlerTypes) {
        val newSearchPayload = uiState.searchPayload.clone()
        newSearchPayload.source = source
        newSearchPayload.page = 1

        uiState = uiState.copy(
            searchPayload = newSearchPayload
        )
    }

    private var fetchJob: Job? = null
    fun searchMangas(searchOpts: SearchCrawlerPayload, append: Boolean) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val response = mangaRepository.searchMangas(searchOpts)

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        mangas = if (append) response.value?.let { uiState.mangas + it } ?: emptyList() else response.value ?: emptyList(),
                        errors = null,
                        canLoadMore = response.value?.isNotEmpty() ?: false
                    )
                } else {
                    uiState.copy(
                        errors = response.errors,
                        canLoadMore = false
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to search crawlers.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    errors = AppErrors(listOf(QueryCrawlerErrors.UNKNOWN_ERROR)),
                    canLoadMore = false
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

                // Assume application created
                return (application as SmithersApplication).mangaRepository?.let {
                    MangaSearchViewModel(
                        it
                    )
                } as T
            }
        }
    }
}
