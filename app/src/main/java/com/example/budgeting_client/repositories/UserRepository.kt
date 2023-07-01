package com.example.budgeting_client.repositories

import android.util.Log
import com.example.budgeting_client.models.AppResult
import com.example.budgeting_client.models.AppAuthUserErrors
import com.example.budgeting_client.models.AuthUser
import com.example.budgeting_client.models.User
import com.example.budgeting_client.models.AuthUserErrors
import com.example.budgeting_client.utils.AppErrors
import com.google.gson.Gson

class UserRepository constructor(
    private val remoteSource: UserNetworkDataSource,
    private val gson: Gson
) {
    suspend fun getMyUserInfo(): AppResult<User, AuthUserErrors> {
        try {
            val response = remoteSource.getMyUserInfo()

            return when (response.code()) {
                200 -> {
                    val body = response.body()

                    if (body == null) {
                        AppResult(
                            isSuccessful = false,
                            value = null,
                            errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                        )
                    } else {
                        AppResult(
                            isSuccessful = true,
                            value = User(body),
                            errors = null
                        )
                    }
                }
                401 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNAUTHORIZED))
                    )
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to login. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to login.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
            )
        }
    }

    suspend fun login(user: AuthUser): AppResult<User, AuthUserErrors> {
        try {
            val response = remoteSource.login(user)

            return when (response.code()) {
                200 -> {
                    val body = response.body()

                    if (body == null) {
                        AppResult(
                            isSuccessful = false,
                            value = null,
                            errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                        )
                    } else {
                        AppResult(
                            isSuccessful = true,
                            value = User(body),
                            errors = null
                        )
                    }
                }
                400 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = response.errorBody()?.let {
                            gson.fromJson(
                                it.string(),
                                AppAuthUserErrors
                            )
                        } ?: AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                    )
                }
                401 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNAUTHORIZED))
                    )
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to login. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to login.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
            )
        }
    }

    suspend fun logout(): AppResult<User, AuthUserErrors> {
        try {
            val response = remoteSource.logout()

            return when (response.code()) {
                200 -> {
                    AppResult(
                        isSuccessful = true,
                        value = null,
                        errors = null
                    )
                }
                401 -> {
                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNAUTHORIZED))
                    )
                }
                else -> {
                    Log.e("BUDGETING_ERROR", "Unable to logout. Server responded with ${response.code()}.")

                    AppResult(
                        isSuccessful = false,
                        value = null,
                        errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BUDGETING_ERROR", e.message ?: "Unable to logout.")
            Log.e("BUDGETING_ERROR", e.stackTraceToString())

            return AppResult(
                isSuccessful = false,
                value = null,
                errors = AppErrors(listOf(AuthUserErrors.UNKNOWN_ERROR))
            )
        }
    }
}