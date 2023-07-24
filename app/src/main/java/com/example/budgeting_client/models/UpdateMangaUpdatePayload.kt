package com.example.budgeting_client.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReadStatus(
    val isRead: Boolean
) : Parcelable

@Parcelize
data class UpdateReadStatusPayload(
    val properties: List<String> = listOf("isRead"),
    val data: ReadStatus,
) : Parcelable