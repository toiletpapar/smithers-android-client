package com.example.budgeting_client.crawler

import android.os.Parcelable
import com.example.budgeting_client.network.retrofit
import kotlinx.parcelize.Parcelize
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date

interface CrawlerService {
//    @GET("crawl-targets")
//    suspend fun getCrawlers(): List<Crawler>

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