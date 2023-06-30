package com.example.budgeting_client.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.budgeting_client.models.AppError

class AppErrors<T : AppError>(private val errors: List<T>?) {
    fun createErrorComposable(hasErrors: List<T>): (@Composable () -> Unit)? {
        if (hasErrors.isEmpty()) {
            throw Error("Called createErrorComposable without any errors to compare against.")
        }

        hasErrors.forEach { appError ->
            if (hasOneOfError(listOf(appError))) {
                val el = @Composable {
                    Text(appError.message)
                }

                return el
            }
        }

        return null
    }

    fun hasOneOfError(hasErrors: List<T>): Boolean {
        return errors?.any { error -> hasErrors.any { appError -> error.name === appError.name} } ?: false
    }
}

