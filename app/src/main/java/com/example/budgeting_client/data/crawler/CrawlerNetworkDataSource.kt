package com.example.budgeting_client.data.crawler

import com.example.budgeting_client.utils.AppErrors
import com.example.budgeting_client.utils.getNullable
import com.example.budgeting_client.utils.parseDate
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.lang.reflect.Type
import java.util.Date

data class CrawlerApiModel(
    var crawlTargetId: Int? = null,
    var name: String,
    var url: String,
    var adapter: CrawlerTypes,
    var lastCrawledOn: Date? = null,
    var crawlSuccess: Boolean? = null
) {
    constructor(crawler: Crawler) : this(
        name = crawler.name,
        url = crawler.url,
        adapter = crawler.adapter
    )
}

// Defines how to access crawler data in the data source
interface CrawlerNetworkDataSource {
    @GET("v1/crawl-targets")
    suspend fun getCrawlers(): Response<List<CrawlerApiModel>>

    @POST("v1/crawl-targets")
    suspend fun saveCrawler(@Body crawler: CrawlerApiModel): Response<CrawlerApiModel>
}

class CrawlerApiModelSerializer : JsonSerializer<CrawlerApiModel>, JsonDeserializer<CrawlerApiModel> {
    override fun serialize(
        crawler: CrawlerApiModel,
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

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CrawlerApiModel? {
        val jsonObject = json?.asJsonObject ?: return null

        val crawlTargetId = jsonObject.getNullable("crawlTargetId")?.asInt
        val name = jsonObject.getNullable("name")?.asString ?: return null
        val url = jsonObject.getNullable("url")?.asString ?: return null

        val adapterString = jsonObject.getNullable("adapter")?.asString
        val adapter = CrawlerTypes.values().find { it.value == adapterString } ?: return null

        val lastCrawledOnString = jsonObject.getNullable("lastCrawledOn")?.asString
        val lastCrawledOn = lastCrawledOnString?.let { parseDate(it) }

        val crawlSuccess = jsonObject.getNullable("crawlSuccess")?.asBoolean

        return CrawlerApiModel(
            crawlTargetId = crawlTargetId,
            name = name,
            url = url,
            adapter = adapter,
            lastCrawledOn = lastCrawledOn,
            crawlSuccess = crawlSuccess
        )
    }
}

class CrawlerApiErrorModelDeserializer : JsonDeserializer<AppErrors> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AppErrors? {
        val jsonObject = json?.asJsonObject ?: return null

        val appErrors =  AppErrors(jsonObject.getAsJsonArray("errors").map {
            val error = it.asJsonObject
            when (error.getNullable("type")?.asString) {
                "required" -> when (error.getNullable("path")?.asString) {
                    "name" -> CrawlerErrors.EMPTY_NAME
                    "url" -> CrawlerErrors.EMPTY_URL
                    else -> CrawlerErrors.UNKNOWN_ERROR
                }
                "nullable" -> when (error.getNullable("path")?.asString) {
                    "name" -> CrawlerErrors.EMPTY_NAME
                    "url" -> CrawlerErrors.EMPTY_URL
                    else -> CrawlerErrors.UNKNOWN_ERROR
                }
                "url" -> when (error.getNullable("path")?.asString) {
                    "url" -> CrawlerErrors.INVALID_URL
                    else -> CrawlerErrors.UNKNOWN_ERROR
                }
                else -> CrawlerErrors.UNKNOWN_ERROR
            }
        })

        return appErrors
    }
}

