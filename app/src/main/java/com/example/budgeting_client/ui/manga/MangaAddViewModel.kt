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
import com.example.budgeting_client.models.crawler.CrawlerErrors
import com.example.budgeting_client.models.crawler.CreateCrawlerPayload
import com.example.budgeting_client.repositories.MangaRepository
import com.example.budgeting_client.utils.AppErrors
import kotlinx.coroutines.launch

// initial state
data class MangaAddUiState (
    val isSaveComplete: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnknownError: Boolean = false,
    val errors: AppErrors? = null
)

// reduce
class MangaAddViewModel constructor(
    private val mangaRepository: MangaRepository
) : ViewModel() {
    var uiState by mutableStateOf(MangaAddUiState())
        private set

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
                Log.e("BUDGETING_ERROR", e.message ?: "Unable to retrieve crawlers.")
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

                return MangaAddViewModel(
                    (application as SmithersApplication).mangaRepository
                ) as T
            }
        }
    }
}
