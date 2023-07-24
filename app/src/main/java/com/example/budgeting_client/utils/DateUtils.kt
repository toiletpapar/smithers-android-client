package com.example.budgeting_client.utils

import android.annotation.SuppressLint
import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

@SuppressLint("SimpleDateFormat")
fun parseDate(dateString: String): Date? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX")
    return try {
        format.parse(dateString)
    } catch (e: ParseException) {
        Log.e("MY ERROR", e.message ?: "COULD NOT PARSE DATE")
        null
    }
}