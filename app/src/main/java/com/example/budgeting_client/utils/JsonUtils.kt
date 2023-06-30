package com.example.budgeting_client.utils

import com.example.budgeting_client.models.AppCrawlerErrors
import com.example.budgeting_client.models.AppAuthUserErrors
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.repositories.AuthUserApiErrorModelDeserializer
import com.example.budgeting_client.repositories.CrawlerApiModel
import com.example.budgeting_client.repositories.CrawlerApiModelDeserializer
import com.example.budgeting_client.repositories.CreateCrawlerPayloadSerializer
import com.example.budgeting_client.repositories.MangaApiErrorModelDeserializer
import com.example.budgeting_client.repositories.MangaApiModel
import com.example.budgeting_client.repositories.MangaApiModelSerializer
import com.example.budgeting_client.repositories.MangaUpdateApiModel
import com.example.budgeting_client.repositories.MangaUpdateApiModelDeserializer
import com.example.budgeting_client.repositories.UserApiModel
import com.example.budgeting_client.repositories.UserApiModelDeserializer
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

fun initializeGson(): Gson {
    // Builder for types depended on in other serializers
    val parentGsonBuilder: GsonBuilder = GsonBuilder()
        .registerTypeAdapter(CrawlerApiModel::class.java, CrawlerApiModelDeserializer())
        .registerTypeAdapter(MangaUpdateApiModel::class.java, MangaUpdateApiModelDeserializer())
        .serializeNulls()

    return parentGsonBuilder
        .registerTypeAdapter(MangaApiModel::class.java, MangaApiModelSerializer(parentGsonBuilder.create()))
        .registerTypeAdapter(MangaUpdateApiModel::class.java, MangaUpdateApiModelDeserializer())
        .registerTypeAdapter(AppCrawlerErrors, MangaApiErrorModelDeserializer())
        .registerTypeAdapter(AppAuthUserErrors, AuthUserApiErrorModelDeserializer())
        .registerTypeAdapter(CrawlerApiModel::class.java, CrawlerApiModelDeserializer())
        .registerTypeAdapter(CreateCrawlerPayload::class.java, CreateCrawlerPayloadSerializer())
        .registerTypeAdapter(UserApiModel::class.java, UserApiModelDeserializer())
        .create()
}