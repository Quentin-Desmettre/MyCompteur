package com.example.compteur.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class StravaTokenResponse(
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "expires_at") val expiresAt: Long
)

@JsonClass(generateAdapter = true)
data class StravaUploadResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "id_str") val idStr: String,
    @Json(name = "external_id") val externalId: String?,
    @Json(name = "error") val error: String?,
    @Json(name = "status") val status: String?,
    @Json(name = "activity_id") val activityId: Long?
)

interface StravaApi {

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun exchangeToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String = "authorization_code"
    ): StravaTokenResponse

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun refreshToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): StravaTokenResponse

    @Multipart
    @POST("api/v3/uploads")
    suspend fun uploadActivity(
        @Query("access_token") accessToken: String,
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody?,
        @Part("description") description: RequestBody?,
        @Part("trainer") trainer: RequestBody?,
        @Part("commute") commute: RequestBody?,
        @Part("data_type") dataType: RequestBody,
        @Part("external_id") externalId: RequestBody?
    ): StravaUploadResponse

    @retrofit2.http.GET("api/v3/uploads/{uploadId}")
    suspend fun getUploadStatus(
        @retrofit2.http.Path("uploadId") uploadId: Long,
        @Query("access_token") accessToken: String
    ): StravaUploadResponse

    @FormUrlEncoded
    @retrofit2.http.PUT("api/v3/activities/{id}")
    suspend fun updateActivity(
        @retrofit2.http.Path("id") activityId: Long,
        @Query("access_token") accessToken: String,
        @Field("hide_from_home") hideFromHome: Boolean? = null
    ): okhttp3.ResponseBody
}
