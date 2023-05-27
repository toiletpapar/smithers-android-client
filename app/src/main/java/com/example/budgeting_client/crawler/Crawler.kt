package com.example.budgeting_client.crawler

import android.os.Parcelable
import com.example.budgeting_client.network.retrofit
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.lang.reflect.Type
import java.util.Date

interface CrawlerService {
    @GET("crawl-targets")
    suspend fun getCrawlers(): Response<List<Crawler>>

    @POST("crawl-targets")
    suspend fun createCrawler(@Body crawler: Crawler): Response<Crawler>
}

val crawlerService: CrawlerService = retrofit.create(CrawlerService::class.java)

enum class CrawlerTypes(val displayName: String, val value: String) {
    WEBTOON("Webtoon", "webtoon"),
    MANGADEX("Mangadex", "mangadex")
}

@Parcelize
data class Crawler(
    var crawlTargetId: Int? = null,
    var name: String,
    var url: String,
    var adapter: CrawlerTypes,
    var lastCrawledOn: Date? = null,
    var crawlSuccess: Boolean? = null
): Parcelable {
}

class CrawlerSerializer : JsonSerializer<Crawler> {
    override fun serialize(
        crawler: Crawler,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val crawlerJson = JsonObject()

        if (crawler.crawlTargetId != null) {
            crawlerJson.addProperty("crawlTargetId", crawler.crawlTargetId)
        }
        crawlerJson.addProperty("name", crawler.name)
        crawlerJson.addProperty("url", crawler.url)
        crawlerJson.addProperty("adapter", crawler.adapter.value)
        crawlerJson.addProperty("lastCrawledOn", crawler.lastCrawledOn?.time)
        crawlerJson.addProperty("crawlSuccess", crawler.crawlSuccess)

        return crawlerJson
    }
}