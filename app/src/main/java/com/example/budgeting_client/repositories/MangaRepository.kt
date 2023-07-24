package com.example.budgeting_client.repositories

import android.util.Log
import com.example.budgeting_client.models.AppCrawlerErrors
import com.example.budgeting_client.models.AppResult
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.Manga
import com.example.budgeting_client.models.UpdateCrawlerPayload
import com.example.budgeting_client.utils.AppErrors
import com.google.gson.Gson

// Defines how to access crawler data in the application
class MangaRepository constructor(
    private val remoteSource: MangaNetworkDataSource,
    private val gson: Gson
) {
    suspend fun getMangas(): AppResult<List<Manga>, CrawlerErrors> {
        try {
            val response = remoteSource.getManga()

            return when (response.code()) {
                200 -> {
                    val body = response.body()

                    if (body == null) {
                        AppResult(
                            isSuccessful = false,
                            value = null,
                            errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                        )
                    } else {
                        AppResult(
                            isSuccessful = true,
                            value = body.map { model -> Manga(model) },
                            errors = null
                        )
                    }
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to getCrawlers. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                    )
                }
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

    suspend fun syncManga(crawlTargetId: Int): AppResult<Unit, CrawlerErrors> {
        try {
            val response = remoteSource.syncManga(crawlTargetId)

            return when (response.code()) {
                200 -> {
                    AppResult(
                        isSuccessful = true,
                        value = null,
                        errors = null
                    )
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to sync crawler. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to sync crawler.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
            )
        }
    }

    suspend fun getCrawler(crawlTargetId: Int): AppResult<Manga, CrawlerErrors> {
        try {
            val response = remoteSource.getCrawler(crawlTargetId)

            return when (response.code()) {
                200 -> {
                    val body = response.body()

                    if (body == null) {
                        AppResult(
                            isSuccessful = false,
                            value = null,
                            errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                        )
                    } else {
                        AppResult(
                            isSuccessful = true,
                            value = Manga(MangaApiModel(body, listOf())),
                            errors = null
                        )
                    }
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to getCrawler. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to getCrawler.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
            )
        }
    }

    suspend fun saveCrawler(crawler: CreateCrawlerPayload): AppResult<Unit, CrawlerErrors> {
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
                        errors = response.errorBody()?.let {
                            gson.fromJson(
                                it.string(),
                                AppCrawlerErrors
                            )
                        } ?: AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
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

    suspend fun updateCrawler(crawlTargetId: Int, crawlerUpdate: UpdateCrawlerPayload): AppResult<Unit, CrawlerErrors> {
        try {
            val response = remoteSource.updateCrawler(crawlTargetId, crawlerUpdate)

            return when (response.code()) {
                200 -> AppResult(
                    isSuccessful = true,
                    value = null,
                    errors = null
                )
                400 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = response.errorBody()?.let {
                            gson.fromJson(
                                it.string(),
                                AppCrawlerErrors
                            )
                        } ?: AppErrors(listOf(CrawlerErrors.UNKNOWN_ERROR))
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