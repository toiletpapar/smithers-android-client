package com.example.budgeting_client.models

import android.os.Parcelable
import com.example.budgeting_client.utils.AppErrors
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

@Parcelize
data class SearchCrawlerPayload(
    var query: String,
    var page: Short,
    var source: CrawlerTypes,
) : Parcelable {
    fun clone(): SearchCrawlerPayload {
        return SearchCrawlerPayload(
            query = this.query,
            page = this.page,
            source = this.source
        )
    }
}

enum class QueryCrawlerErrors(override val message: String) : AppError {
    EMPTY_QUERY("Search cannot be empty"),
    UNKNOWN_ERROR("Something wasn't quite right. Try again in a few moments.")
}

val AppQueryCrawlerErrors: Type? = TypeToken.getParameterized(AppErrors::class.java, QueryCrawlerErrors::class.java).type