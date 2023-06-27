package com.example.budgeting_client.models

import android.os.Parcelable
import com.example.budgeting_client.repositories.MangaApiModel
import com.example.budgeting_client.repositories.MangaUpdateApiModel
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Manga(
    val crawlTargetId: Int,
    val name: String,
    val url: String,
    val adapter: CrawlerTypes,
    val lastCrawledOn: Date?,
    val crawlSuccess: Boolean?,
    val updates: List<MangaUpdate>
): Parcelable {
    constructor(mangaApiModel: MangaApiModel): this(
        crawlTargetId = mangaApiModel.crawler.crawlTargetId,
        name = mangaApiModel.crawler.name,
        url = mangaApiModel.crawler.url,
        adapter = mangaApiModel.crawler.adapter,
        lastCrawledOn = mangaApiModel.crawler.lastCrawledOn,
        crawlSuccess = mangaApiModel.crawler.crawlSuccess,
        updates = mangaApiModel.mangaUpdates.map<MangaUpdateApiModel, MangaUpdate> {
            MangaUpdate(it)
        }
    )
}

@Parcelize
data class MangaUpdate(
    val latestMangaUpdateId: Int,
    val crawledOn: Date,
    val chapter: Short,
    val chapterName: String? = null,
    val isRead: Boolean,
    val readAt: String
) : Parcelable {
    constructor(mangaUpdate: MangaUpdateApiModel) : this(
        latestMangaUpdateId = mangaUpdate.latestMangaUpdateId,
        crawledOn = mangaUpdate.crawledOn,
        chapter = mangaUpdate.chapter,
        chapterName = mangaUpdate.chapterName,
        isRead = mangaUpdate.isRead,
        readAt = mangaUpdate.readAt
    )
}