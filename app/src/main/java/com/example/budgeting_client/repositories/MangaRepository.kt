package com.example.budgeting_client.repositories

import android.util.Log
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.Manga
import com.example.budgeting_client.models.AppResult
import com.example.budgeting_client.utils.AppErrors
import com.example.budgeting_client.utils.gson

// Defines how to access crawler data in the application
class MangaRepository constructor(
    private val remoteSource: MangaNetworkDataSource
) {
    suspend fun getMangas(): AppResult<List<Manga>> {
        try {
            val response = remoteSource.getManga()

            return if (response.code() == 200) {
                AppResult(
                    isSuccessful = true,
                    value = response.body()?.map { model -> Manga(model) } ?: emptyList(),
                    errors = null
                )
            } else {
                Log.e("BUDGETING_ERROR", "Unable to getCrawlers. Server responded with ${response.code()}.")

                AppResult(
                    isSuccessful = false,
                    value = null,
                    errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                )
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to getCrawlers.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
            )
        }
    }

    suspend fun saveCrawler(crawler: CreateCrawlerPayload): AppResult<Unit> {
        try {
            val response = remoteSource.saveCrawler(crawler)

            return when (response.code()) {
                201 -> AppResult(
                    isSuccessful = true,
                    value = null,
                    errors = null
                )
                400, 409 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = gson.fromJson(
                            response.errorBody()!!.string(),
                            AppErrors::class.java
                        )
                    )
                }
                else -> {
                    Log.e(
                        "BUDGETING_ERROR",
                        "Unable to getCrawlers. Server responded with ${response.code()}."
                    )

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to saveCrawlers.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
            )
        }
    }
}