package com.example.budgeting_client.models.manga

import android.os.Parcelable
import com.example.budgeting_client.repositories.MangaUpdateApiModel
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class MangaUpdate(
    val latestMangaUpdateId: Int,
    val crawledOn: Date,
    val chapter: Int,
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