package com.example.budgeting_client.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UpdateCrawlerPayload(
    val properties: List<String> = listOf("name", "url", "adapter"),
    val data: CreateCrawlerPayload,
) : Parcelable