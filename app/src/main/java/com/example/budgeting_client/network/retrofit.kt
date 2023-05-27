package com.example.budgeting_client.network

import com.example.budgeting_client.crawler.Crawler
import com.example.budgeting_client.crawler.CrawlerSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val gson: Gson = GsonBuilder()
    .registerTypeAdapter(Crawler::class.java, CrawlerSerializer())
    .serializeNulls()
    .create()
val retrofit: Retrofit = Retrofit.Builder()
    // TODO: Replace domain here and in network-security-config
    .baseUrl("http://10.0.2.2:8080/api/v1/")
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()