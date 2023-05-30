package com.example.budgeting_client.result

import com.example.budgeting_client.utils.AppErrors

interface AppError {
    val name: String
    val message: String
}

data class AppResult<T>(val isSuccessful: Boolean, val value: T?, val errors: AppErrors?) {}