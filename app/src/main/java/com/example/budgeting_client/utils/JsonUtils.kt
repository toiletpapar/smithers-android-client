package com.example.budgeting_client.utils

import com.example.budgeting_client.models.AppAuthUserErrors
import com.example.budgeting_client.models.AppCrawlerErrors
import com.example.budgeting_client.models.AppQueryCrawlerErrors
import com.example.budgeting_client.models.CreateCrawlerPayload
import com.example.budgeting_client.models.UpdateCrawlerFavouritePayload
import com.example.budgeting_client.models.UpdateCrawlerPayload
import com.example.budgeting_client.models.UpdateReadStatusPayload
import com.example.budgeting_client.repositories.AuthUserApiErrorModelDeserializer
import com.example.budgeting_client.repositories.CrawlerApiModel
import com.example.budgeting_client.repositories.CrawlerApiModelDeserializer
import com.example.budgeting_client.repositories.CreateCrawlerPayloadSerializer
import com.example.budgeting_client.repositories.MangaApiErrorModelDeserializer
import com.example.budgeting_client.repositories.MangaApiModel
import com.example.budgeting_client.repositories.MangaApiModelDeserializer
import com.example.budgeting_client.repositories.MangaSearchApiErrorModelDeserializer
import com.example.budgeting_client.repositories.MangaUpdateApiModel
import com.example.budgeting_client.repositories.MangaUpdateApiModelDeserializer
import com.example.budgeting_client.repositories.UpdateCrawlerFavouritePayloadSerializer
import com.example.budgeting_client.repositories.UpdateCrawlerPayloadSerializer
import com.example.budgeting_client.repositories.UpdateReadStatusPayloadSerializer
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
        .registerTypeAdapter(CreateCrawlerPayload::class.java, CreateCrawlerPayloadSerializer())
        .serializeNulls()

    val parentGson = parentGsonBuilder.create()

    return parentGsonBuilder
        .registerTypeAdapter(MangaApiModel::class.java, MangaApiModelDeserializer(parentGson))
        .registerTypeAdapter(MangaUpdateApiModel::class.java, MangaUpdateApiModelDeserializer())
        .registerTypeAdapter(AppCrawlerErrors, MangaApiErrorModelDeserializer())
        .registerTypeAdapter(AppQueryCrawlerErrors, MangaSearchApiErrorModelDeserializer())
        .registerTypeAdapter(AppAuthUserErrors, AuthUserApiErrorModelDeserializer())
        .registerTypeAdapter(CrawlerApiModel::class.java, CrawlerApiModelDeserializer())
        .registerTypeAdapter(UserApiModel::class.java, UserApiModelDeserializer())
        .registerTypeAdapter(UpdateCrawlerPayload::class.java, UpdateCrawlerPayloadSerializer(parentGson))
        .registerTypeAdapter(UpdateReadStatusPayload::class.java, UpdateReadStatusPayloadSerializer())
        .registerTypeAdapter(UpdateCrawlerFavouritePayload::class.java, UpdateCrawlerFavouritePayloadSerializer())
        .create()
}