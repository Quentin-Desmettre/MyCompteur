package com.example.compteur.data.repository

import com.example.compteur.data.api.StravaApi
import com.example.compteur.domain.repository.StravaRepository
import com.example.compteur.utils.StravaConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StravaRepositoryImpl @Inject constructor(
    private val stravaApi: StravaApi,
    private val settingsRepository: SettingsRepository
) : StravaRepository {

    override val isConnected: Flow<Boolean> = settingsRepository.stravaAccessTokenFlow.map { it != null }

    override suspend fun authenticate(code: String): Result<Unit> {
        return try {
            val response = stravaApi.exchangeToken(
                clientId = StravaConstants.CLIENT_ID,
                clientSecret = StravaConstants.CLIENT_SECRET,
                code = code
            )
            settingsRepository.saveStravaTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken,
                expiresAt = response.expiresAt
            )
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override suspend fun disconnect() {
        settingsRepository.clearStravaTokens()
    }

    private suspend fun getValidAccessToken(): String? {
        val accessToken = settingsRepository.stravaAccessTokenFlow.firstOrNull() ?: return null
        val expiresAt = settingsRepository.stravaExpiresAtFlow.firstOrNull() ?: 0L

        // If token expires in less than 5 minutes, refresh it
        val currentTimeSeconds = System.currentTimeMillis() / 1000
        if (expiresAt < currentTimeSeconds + 300) {
            val refreshToken = settingsRepository.stravaRefreshTokenFlow.firstOrNull() ?: return null
            try {
                val response = stravaApi.refreshToken(
                    clientId = StravaConstants.CLIENT_ID,
                    clientSecret = StravaConstants.CLIENT_SECRET,
                    refreshToken = refreshToken
                )
                settingsRepository.saveStravaTokens(
                    accessToken = response.accessToken,
                    refreshToken = response.refreshToken,
                    expiresAt = response.expiresAt
                )
                return response.accessToken
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
        return accessToken
    }

    override suspend fun uploadActivity(
        fitFile: File, 
        name: String, 
        description: String,
        isCommute: Boolean,
        isTrainer: Boolean,
        isPrivate: Boolean
    ): Result<Unit> {
        return try {
            val accessToken = getValidAccessToken() ?: return Result.failure(Exception("Not authenticated"))

            val requestFile = fitFile.asRequestBody("application/vnd.ant.fit".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fitFile.name, requestFile)
            val nameBody = name.toRequestBody(MultipartBody.FORM)
            val descriptionBody = description.toRequestBody(MultipartBody.FORM)
            val dataTypeBody = "fit".toRequestBody(MultipartBody.FORM)
            val commuteBody = (if (isCommute) "1" else "0").toRequestBody(MultipartBody.FORM)
            val trainerBody = (if (isTrainer) "1" else "0").toRequestBody(MultipartBody.FORM)

            var uploadResponse = stravaApi.uploadActivity(
                accessToken = accessToken,
                file = filePart,
                name = nameBody,
                description = descriptionBody,
                trainer = trainerBody,
                commute = commuteBody,
                dataType = dataTypeBody,
                externalId = null
            )

            if (uploadResponse.error != null) {
                return Result.failure(Exception(uploadResponse.error))
            }

            // Polling for activity_id
            var activityId = uploadResponse.activityId
            var attempts = 0
            while (activityId == null && attempts < 15) {
                delay(2000)
                uploadResponse = stravaApi.getUploadStatus(
                    accessToken = accessToken,
                    uploadId = uploadResponse.id
                )
                if (uploadResponse.error != null) {
                    return Result.failure(Exception(uploadResponse.error))
                }
                activityId = uploadResponse.activityId
                attempts++
            }

            if (activityId == null) {
                return Result.failure(Exception("Le traitement Strava a expiré, réessayez plus tard"))
            }

            if (isPrivate) {
                stravaApi.updateActivity(
                    accessToken = accessToken,
                    activityId = activityId,
                    hideFromHome = true
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
