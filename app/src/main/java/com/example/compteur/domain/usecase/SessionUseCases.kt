package com.example.compteur.domain.usecase

import com.example.compteur.domain.model.Session
import com.example.compteur.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartSessionUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    suspend operator fun invoke(routeId: Long?): Long {
        return repository.startSession(routeId)
    }
}

class GetSessionsUseCase @Inject constructor(
    private val repository: SessionRepository
) {
    operator fun invoke(): Flow<List<Session>> = repository.getAllSessions()
}
