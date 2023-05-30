package com.example.budgeting_client.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.budgeting_client.result.AppError

class AppErrors(private val errors: List<AppError>?) {
    fun createErrorComposable(hasErrors: List<AppError>): (@Composable () -> Unit)? {
        if (hasErrors.isEmpty()) {
            throw Error("Called createErrorComposable without any errors to compare against.")
        }

        hasErrors.forEachIndexed { index, appError ->
            if (hasOneOfError(listOf(appError))) {
                val el = @Composable {
                    Text(hasErrors[index].message)
                }

                return el
            }
        }

        return null
    }

    fun hasOneOfError(hasErrors: List<AppError>): Boolean {
        return errors?.any { error -> hasErrors.any { appError -> error.name === appError.name} } ?: false
    }
}

