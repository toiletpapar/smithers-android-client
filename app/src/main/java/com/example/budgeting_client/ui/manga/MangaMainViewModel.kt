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
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.utils.AppErrors
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// initial state
data class MangaMainUiState (
    val mangas: List<Manga> = emptyList(),
    val hasUnknownError: Boolean = false,
    val ackSync: Boolean = false,
    val errors: AppErrors<CrawlerErrors>? = null
)

// reduce
class MangaMainViewModel constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    var uiState by mutableStateOf(MangaMainUiState())
        private set

    private var fetchJob: Job? = null
    fun getMangas() {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            try {
                val response = mangaRepository.getMangas()

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        mangas = response.value ?: emptyList(),
                        hasUnknownError = false,
                        errors = null
                    )
                } else {
                    uiState.copy(
                        errors = response.errors,
                        hasUnknownError = response.errors?.hasOneOfError(listOf(CrawlerErrors.UNKNOWN_ERROR)) ?: false
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to retrieve crawlers.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR)),
                    hasUnknownError = true
                )
            }
        }
    }

    fun syncManga(crawlTargetId: Int) {
        uiState = uiState.copy(
            ackSync = false
        )

        viewModelScope.launch {
            try {
                val response = mangaRepository.syncManga(crawlTargetId)

                uiState = if (response.isSuccessful) {
                    uiState.copy(
                        hasUnknownError = false,
                        errors = null,
                        ackSync = true
                    )
                } else {
                    uiState.copy(
                        errors = response.errors,
                        hasUnknownError = response.errors?.hasOneOfError(listOf(CrawlerErrors.UNKNOWN_ERROR)) ?: false
                    )
                }
            } catch (e: Exception) {
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to sync crawler.")
                Log.e("BUDGETING_ERROR", e.stackTraceToString())

                uiState = uiState.copy(
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR)),
                    hasUnknownError = true
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
                    MangaMainViewModel(
                        it
                    )
                } as T
            }
        }
    }
}
