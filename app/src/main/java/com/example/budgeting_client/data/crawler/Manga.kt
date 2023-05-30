package com.example.budgeting_client.data.crawler

import android.os.Parcelable
import android.util.Log
import com.example.budgeting_client.result.AppError
import com.example.budgeting_client.result.AppResult
import com.example.budgeting_client.utils.AppErrors
import com.example.budgeting_client.utils.gson
import kotlinx.parcelize.Parcelize

enum class CrawlerErrors(override val message: String) : AppError {
    DUPLICATE_NAME_KEY("Crawler already exists. Choose a different crawler name."),
    EMPTY_NAME("Crawler name is a required field"),
    EMPTY_URL("Crawler url is a required field"),
    INVALID_URL("Crawler should have a valid Url"),
    UNKNOWN_ERROR("Something wasn't quite right. Try again in a few moments.")
}

enum class CrawlerTypes(val displayName: String, val value: String) {
    WEBTOON("Webtoon", "webtoon"),
    MANGADEX("Mangadex", "mangadex")
}

@Parcelize
data class Crawler(
    val crawlTargetId: Int? = null,
    var name: String,
    var url: String,
    var adapter: CrawlerTypes,
): Parcelable {
    constructor(crawlerApiModel: CrawlerApiModel): this(
        crawlTargetId = crawlerApiModel.crawlTargetId,
        name = crawlerApiModel.name,
        url = crawlerApiModel.url,
        adapter = crawlerApiModel.adapter
    )
}

// Defines how to access crawler data in the application
class MangaRepository constructor(
    private val remoteSource: CrawlerNetworkDataSource
) {
    suspend fun getMangas(): AppResult<List<Crawler>> {
        try {
            val response = remoteSource.getCrawlers()

            return if (response.code() == 200) {
                AppResult(
                    isSuccessful = true,
                    value = response.body()?.map { model -> Crawler(model) } ?: emptyList(),
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

    suspend fun saveCrawler(crawler: Crawler): AppResult<Crawler> {
        try {
            val response = remoteSource.saveCrawler(CrawlerApiModel(crawler))

            return if (response.code() == 201) {
                AppResult(
                    isSuccessful = true,
                    value = Crawler(response.body()!!),
                    errors = null
                )
            } else if (response.code() == 409) {
                AppResult(
                    isSuccessful = false,
                    value = null,
                    errors = AppErrors(listOf(CrawlerErrors.DUPLICATE_NAME_KEY))
                )
            } else if (response.code() == 400) {
                @Suppress("UNCHECKED_CAST")
                AppResult(
                    isSuccessful = false,
                    value = null,
                    errors = gson.fromJson(response.errorBody()!!.string(), AppErrors::class.java)
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