package com.example.budgeting_client.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


data class ReadStatus(
    val isRead: Boolean
)

data class UpdateReadStatusPayload(
    val properties: List<String> = listOf("isRead"),
    val data: ReadStatus,
)