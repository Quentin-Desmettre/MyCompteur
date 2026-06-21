package com.example.compteur.domain.repository

import java.io.File
import kotlinx.coroutines.flow.Flow

interface StravaRepository {
    val isConnected: Flow<Boolean>

    suspend fun authenticate(code: String): Result<Unit>
    suspend fun uploadActivity(
        fitFile: File, 
        name: String, 
        description: String,
        isCommute: Boolean = false,
        isTrainer: Boolean = false,
        isPrivate: Boolean = false
    ): Result<Unit>
    suspend fun disconnect()
}
