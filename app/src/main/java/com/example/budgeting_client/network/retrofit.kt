package com.example.budgeting_client.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("localhost:8080/api/v1")
    .addConverterFactory(GsonConverterFactory.create())
    .build()