package com.example.compteur.di

import com.example.compteur.data.gpx.GpxParser
import com.example.compteur.data.repository.RouteRepositoryImpl
import com.example.compteur.data.repository.SessionRepositoryImpl
import com.example.compteur.data.repository.DeviceRepositoryImpl
import com.example.compteur.domain.repository.RouteRepository
import com.example.compteur.domain.repository.SessionRepository
import com.example.compteur.domain.repository.DeviceRepository
import com.example.compteur.data.repository.StravaRepositoryImpl
import com.example.compteur.domain.repository.StravaRepository
import com.example.compteur.data.repository.LiveTrackingRepositoryImpl
import com.example.compteur.domain.repository.LiveTrackingRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(
        sessionRepositoryImpl: SessionRepositoryImpl
    ): SessionRepository

    @Binds
    @Singleton
    abstract fun bindDeviceRepository(
        deviceRepositoryImpl: DeviceRepositoryImpl
    ): DeviceRepository

    @Binds
    @Singleton
    abstract fun bindStravaRepository(
        stravaRepositoryImpl: StravaRepositoryImpl
    ): StravaRepository

    @Binds
    @Singleton
    abstract fun bindLiveTrackingRepository(
        liveTrackingRepositoryImpl: LiveTrackingRepositoryImpl
    ): LiveTrackingRepository
}

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {

    @Provides
    @Singleton
    fun provideGpxParser(): GpxParser = GpxParser()
}
