package com.example.budgeting_client.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun parseDate(dateString: String): Date? {
    // TODO: Locale aware times
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    return try {
        format.parse(dateString)
    } catch (e: ParseException) {
        Log.e("MY ERROR", e.message ?: "COULD NOT PARSE DATE")
        null
    }
}