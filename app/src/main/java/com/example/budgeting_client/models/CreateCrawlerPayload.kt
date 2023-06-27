package com.example.budgeting_client.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreateCrawlerPayload(
    var name: String,
    var url: String,
    var adapter: CrawlerTypes,
) : Parcelable

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