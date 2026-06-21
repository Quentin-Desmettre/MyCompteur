package com.example.compteur.di

import android.content.Context
import androidx.room.Room
import com.example.compteur.data.db.AppDatabase
import com.example.compteur.data.db.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "compteur_database"
        ).build()
    }

    @Provides
    fun provideSessionDao(database: AppDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideGpsPointDao(database: AppDatabase): GpsPointDao = database.gpsPointDao()

    @Provides
    fun provideSensorDataDao(database: AppDatabase): SensorDataDao = database.sensorDataDao()

    @Provides
    fun provideRouteDao(database: AppDatabase): RouteDao = database.routeDao()

    @Provides
    fun provideRoutePointDao(database: AppDatabase): RoutePointDao = database.routePointDao()

    @Provides
    fun provideDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()
}
