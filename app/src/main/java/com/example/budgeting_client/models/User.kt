package com.example.budgeting_client.models

import android.os.Parcelable
import com.example.budgeting_client.repositories.UserApiModel
import com.example.budgeting_client.utils.AppErrors
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize
import java.lang.reflect.Type

enum class AuthUserErrors(override val message: String) : AppError {
    UNAUTHORIZED("Your username or password is incorrect"),
    EMPTY_USERNAME("Username is a required field"),
    EMPTY_PASSWORD("Password is a required field"),
    UNKNOWN_ERROR("Something wasn't quite right. Try again in a few moments.")
}
val AppAuthUserErrors: Type? = TypeToken.getParameterized(AppErrors::class.java, AuthUserErrors::class.java).type

@Parcelize
data class User(
    val userId: Int,
    val username: String
): Parcelable {
    constructor(user: UserApiModel) : this(
        userId = user.userId,
        username = user.username,
    )
}

@Parcelize
data class AuthUser(
    val username: String,
    val password: String
): Parcelable