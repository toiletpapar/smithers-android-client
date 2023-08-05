package com.example.budgeting_client.repositories

import android.util.Log
import com.example.budgeting_client.models.CrawlerErrors
import com.example.budgeting_client.models.CrawlerTypes
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.QueryCrawlerErrors
import com.example.budgeting_client.models.UpdateCrawlerFavouritePayload
import com.example.budgeting_client.models.UpdateCrawlerPayload
import com.example.budgeting_client.models.UpdateReadStatusPayload
import com.example.budgeting_client.utils.AppErrors
import com.example.budgeting_client.utils.getNullable
import com.example.budgeting_client.utils.parseDate
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Type
import java.util.Date

data class CrawlerApiModel(
    val crawlTargetId: Int,
    val name: String,
    val url: String,
    val adapter: CrawlerTypes,
    val lastCrawledOn: Date?,
    val crawlSuccess: Boolean?,
    val favourite: Boolean
)

data class MangaUpdateApiModel(
    val mangaUpdateId: Int,
    val crawledOn: Date,
    val chapter: Float,
    val chapterName: String? = null,
    val isRead: Boolean,
    val readAt: String,
    val dateCreated: Date
)

data class MangaApiModel(
    val crawler: CrawlerApiModel,
    val mangaUpdates: List<MangaUpdateApiModel>
)

class MangaApiModelDeserializer(private val gson: Gson) : JsonDeserializer<MangaApiModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MangaApiModel? {
        val jsonObject = json?.asJsonObject ?: return null

        val crawlerJsonObject = jsonObject.getNullable("crawler")?.asJsonObject ?: return null
        val crawler = gson.fromJson(crawlerJsonObject, CrawlerApiModel::class.java) ?: return null

        val mangaUpdates = jsonObject.getNullable("mangaUpdates")?.asJsonArray?.map {
            gson.fromJson(it, MangaUpdateApiModel::class.java)
        } ?: return null

        if (mangaUpdates.any {
            it == null
        }) {
            return null
        }

        return MangaApiModel(
            crawler = crawler,
            mangaUpdates = mangaUpdates
        )
    }
}

class CrawlerApiModelDeserializer : JsonDeserializer<CrawlerApiModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CrawlerApiModel? {
        val jsonObject = json?.asJsonObject ?: return null

        val crawlTargetId = jsonObject.getNullable("crawlTargetId")?.asInt ?: return null
        val name = jsonObject.getNullable("name")?.asString ?: return null
        val url = jsonObject.getNullable("url")?.asString ?: return null

        val adapterString = jsonObject.getNullable("adapter")?.asString ?: return null
        val adapter = CrawlerTypes.values().find { it.value == adapterString } ?: return null

        val lastCrawledOnString = jsonObject.getNullable("lastCrawledOn")?.asString
        val lastCrawledOn = lastCrawledOnString?.let { parseDate(it) }

        val crawlSuccess = jsonObject.getNullable("crawlSuccess")?.asBoolean
        val favourite = jsonObject.getNullable("favourite")?.asBoolean ?: return null

        return CrawlerApiModel(
            crawlTargetId = crawlTargetId,
            name = name,
            url = url,
            adapter = adapter,
            lastCrawledOn = lastCrawledOn,
            crawlSuccess = crawlSuccess,
            favourite = favourite
        )
    }
}

class MangaUpdateApiModelDeserializer : JsonDeserializer<MangaUpdateApiModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MangaUpdateApiModel? {
        val jsonObject = json?.asJsonObject ?: return null

        val mangaUpdateId = jsonObject.getNullable("mangaUpdateId")?.asInt ?: return null
        val crawledOnString = jsonObject.getNullable("crawledOn")?.asString ?: return null
        val crawledOn = parseDate(crawledOnString) ?: return null
        val chapter = jsonObject.getNullable("chapter")?.asFloat ?: return null
        val chapterName = jsonObject.getNullable("chapterName")?.asString
        val isRead = jsonObject.getNullable("isRead")?.asBoolean ?: return null
        val readAt = jsonObject.getNullable("readAt")?.asString ?: return null
        val dateCreatedString = jsonObject.getNullable("dateCreated")?.asString ?: return null
        val dateCreated = parseDate(dateCreatedString) ?: return null

        return MangaUpdateApiModel(
            mangaUpdateId,
            crawledOn,
            chapter,
            chapterName,
            isRead,
            readAt,
            dateCreated
        )
    }
}

class MangaApiErrorModelDeserializer : JsonDeserializer<AppErrors<CrawlerErrors>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AppErrors<CrawlerErrors>? {
        val jsonObject = json?.asJsonObject ?: return null

        val appErrors =  AppErrors(jsonObject.getAsJsonArray("errors").map {
            val error = it.asJsonObject
            when (error.getNullable("type")?.asString) {
                "duplicate_key" -> when(error.getNullable("path")?.asString) {
                    "name" -> CrawlerErrors.DUPLICATE_NAME_KEY
                    else -> CrawlerErrors.UNKNOWN_ERROR
                }
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

class MangaSearchApiErrorModelDeserializer : JsonDeserializer<AppErrors<QueryCrawlerErrors>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AppErrors<QueryCrawlerErrors>? {
        val jsonObject = json?.asJsonObject ?: return null

        val appErrors =  AppErrors(jsonObject.getAsJsonArray("errors").map {
            val error = it.asJsonObject
            when (error.getNullable("type")?.asString) {
                "required" -> when (error.getNullable("path")?.asString) {
                    "query" -> QueryCrawlerErrors.EMPTY_QUERY
                    else -> QueryCrawlerErrors.UNKNOWN_ERROR
                }
                else -> QueryCrawlerErrors.UNKNOWN_ERROR
            }
        })

        return appErrors
    }
}

class CreateCrawlerPayloadSerializer : JsonSerializer<CreateCrawlerPayload>, JsonDeserializer<CreateCrawlerPayload> {
    override fun serialize(
        crawler: CreateCrawlerPayload,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val crawlerJson = JsonObject()

        crawlerJson.addProperty("name", crawler.name)
        crawlerJson.addProperty("url", crawler.url)
        crawlerJson.addProperty("adapter", crawler.adapter.value)

        return crawlerJson
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CreateCrawlerPayload? {
        val jsonObject = json?.asJsonObject ?: return null

        val name = jsonObject.getNullable("name")?.asString ?: return null
        val url = jsonObject.getNullable("url")?.asString ?: return null
        val adapterString = jsonObject.getNullable("adapter")?.asString ?: return null
        val adapter = CrawlerTypes.values().find { it.value == adapterString } ?: return null

        return CreateCrawlerPayload(
            name = name,
            url = url,
            adapter = adapter
        )
    }
}

class UpdateCrawlerPayloadSerializer(private val gson: Gson) : JsonSerializer<UpdateCrawlerPayload> {
    override fun serialize(
        crawler: UpdateCrawlerPayload,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val payloadJson = JsonObject()

        payloadJson.add("data", gson.toJsonTree(crawler.data, CreateCrawlerPayload::class.java))

        val propertiesJson = JsonArray()
        crawler.properties.forEach {
            propertiesJson.add(it)
        }
        payloadJson.add("properties", propertiesJson)

        return payloadJson
    }
}

class UpdateReadStatusPayloadSerializer : JsonSerializer<UpdateReadStatusPayload> {
    override fun serialize(
        payload: UpdateReadStatusPayload,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val payloadJson = JsonObject()

        val dataJson = JsonObject()
        dataJson.addProperty("isRead", payload.data.isRead)

        payloadJson.add("data", dataJson)

        val propertiesJson = JsonArray()
        payload.properties.forEach {
            propertiesJson.add(it)
        }
        payloadJson.add("properties", propertiesJson)

        return payloadJson
    }
}

class UpdateCrawlerFavouritePayloadSerializer : JsonSerializer<UpdateCrawlerFavouritePayload> {
    override fun serialize(
        payload: UpdateCrawlerFavouritePayload,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        val payloadJson = JsonObject()

        val dataJson = JsonObject()
        dataJson.addProperty("favourite", payload.data.isFavourite)

        payloadJson.add("data", dataJson)

        val propertiesJson = JsonArray()
        payload.properties.forEach {
            propertiesJson.add(it)
        }
        payloadJson.add("properties", propertiesJson)

        return payloadJson
    }
}

// Defines how to access crawler data in the data source
interface MangaNetworkDataSource {
    @GET("api/v1/manga")
    suspend fun getManga(@Query("onlyLatest") onlyLatest: Boolean? = true): Response<List<MangaApiModel>>

    @GET("api/v1/crawl-targets/search")
    suspend fun searchCrawlers(@Query("query") query: String, @Query("source") source: CrawlerTypes, @Query("page") page: Short): Response<List<CreateCrawlerPayload>>

    @GET("api/v1/crawl-targets/{crawlTargetId}")
    suspend fun getCrawler(@Path("crawlTargetId") crawlTargetId: Int): Response<CrawlerApiModel>

    @POST("api/v1/crawl-targets")
    suspend fun saveCrawler(@Body crawler: CreateCrawlerPayload): Response<CrawlerApiModel>

    @PATCH("api/v1/crawl-targets/{crawlTargetId}")
    suspend fun updateCrawler(@Path("crawlTargetId") crawlTargetId: Int, @Body updateCrawler: UpdateCrawlerPayload): Response<CrawlerApiModel>

    @PATCH("api/v1/crawl-targets/{crawlTargetId}/favourite")
    suspend fun updateFavouriteCrawler(@Path("crawlTargetId") crawlTargetId: Int, @Body updateCrawler: UpdateCrawlerFavouritePayload): Response<CrawlerApiModel>

    @PATCH("api/v1/manga/{crawlTargetId}")
    suspend fun syncManga(@Path("crawlTargetId") crawlTargetId: Int): Response<Unit>

    @PUT("api/v1/manga-update/{mangaUpdateId}/isRead")
    suspend fun updateReadStatus(@Path("mangaUpdateId") mangaUpdateId: Int, @Body readStatus: UpdateReadStatusPayload): Response<MangaUpdateApiModel>
}

