package com.example.budgeting_client.utils

import android.content.Context
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.example.budgeting_client.SmithersApplication
import okhttp3.OkHttpClient
import java.io.InputStream

@GlideModule
class GlideApp : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val client = OkHttpClient.Builder().build()

        val appHttpClient = (context.applicationContext as SmithersApplication).client
        val factory = if (appHttpClient == null) {
            Log.e("BUDGETING_ERROR", "Application HttpClient was not initialized, falling back to default implementation")
            OkHttpUrlLoader.Factory(client)
        } else {
            OkHttpUrlLoader.Factory(appHttpClient)
        }

        registry.replace(GlideUrl::class.java, InputStream::class.java, factory)
    }
}