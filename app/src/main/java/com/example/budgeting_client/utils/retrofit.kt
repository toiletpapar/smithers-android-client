package com.example.budgeting_client.utils

import android.webkit.CookieManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Replace domain here and in network-security-config
const val HOST = "http://10.0.2.2:8080/"
class CookieInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Add stored cookies to the request headers
        val cookieManager = CookieManager.getInstance()
        val storedCookies = cookieManager.getCookie(HOST)
        if (storedCookies != null && storedCookies.isNotEmpty()) {
            requestBuilder.addHeader("Cookie", storedCookies)
        }

        // Proceed with the request and receive the response
        val response = chain.proceed(requestBuilder.build())

        // Check if the response contains any Set-Cookie headers
        val cookieHeaders = response.headers("Set-Cookie")

        // Process and store the received cookies
        if (cookieHeaders.isNotEmpty()) {
            cookieManager.setCookie(HOST, cookieHeaders.joinToString(separator = "; "))
        }

        return response
    }
}

val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(CookieInterceptor()).build()
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl(HOST)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .build()