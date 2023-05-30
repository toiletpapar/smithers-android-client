package com.example.budgeting_client.utils

import com.example.budgeting_client.data.crawler.CrawlerApiErrorModelDeserializer
import com.example.budgeting_client.data.crawler.CrawlerApiModel
import com.example.budgeting_client.data.crawler.CrawlerApiModelSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun JsonObject.getNullable(key: String): JsonElement? {
    val value: JsonElement = this.get(key) ?: return null

    if (value.isJsonNull) {
        return null
    }

    return value
}

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(CrawlerApiModel::class.java, CrawlerApiModelSerializer())
    .registerTypeAdapter(AppErrors::class.java, CrawlerApiErrorModelDeserializer())
    .serializeNulls()
    .create()