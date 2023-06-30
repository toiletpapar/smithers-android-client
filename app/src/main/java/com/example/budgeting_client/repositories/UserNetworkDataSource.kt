package com.example.budgeting_client.repositories

import com.example.budgeting_client.models.AuthUser
import com.example.budgeting_client.models.AuthUserErrors
import com.example.budgeting_client.utils.AppErrors
import com.example.budgeting_client.utils.getNullable
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import java.lang.reflect.Type

data class UserApiModel(
    val userId: Int,
    val username: String
)

class UserApiModelDeserializer : JsonDeserializer<UserApiModel> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): UserApiModel? {
        val jsonObject = json?.asJsonObject ?: return null

        val userId = jsonObject.getNullable("userId")?.asInt ?: return null
        val username = jsonObject.getNullable("username")?.asString ?: return null

        return UserApiModel(
            userId,
            username,
        )
    }
}

class AuthUserApiErrorModelDeserializer : JsonDeserializer<AppErrors<AuthUserErrors>> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): AppErrors<AuthUserErrors>? {
        val jsonObject = json?.asJsonObject ?: return null

        val appErrors =  AppErrors(jsonObject.getAsJsonArray("errors").map {
            val error = it.asJsonObject
            when (error.getNullable("type")?.asString) {
                "required" -> when (error.getNullable("path")?.asString) {
                    "username" -> AuthUserErrors.EMPTY_USERNAME
                    "password" -> AuthUserErrors.EMPTY_PASSWORD
                    else -> AuthUserErrors.UNKNOWN_ERROR
                }
                "nullable" -> when (error.getNullable("path")?.asString) {
                    "username" -> AuthUserErrors.EMPTY_USERNAME
                    "password" -> AuthUserErrors.EMPTY_PASSWORD
                    else -> AuthUserErrors.UNKNOWN_ERROR
                }
                else -> AuthUserErrors.UNKNOWN_ERROR
            }
        })

        return appErrors
    }
}

interface UserNetworkDataSource {
    @POST("auth/v1/login")
    suspend fun login(@Body user: AuthUser): Response<UserApiModel>

    @DELETE("auth/v1/logout")
    suspend fun logout(): Response<Unit>
}