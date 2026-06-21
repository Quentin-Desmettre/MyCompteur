package com.example.compteur.di

import com.example.compteur.BuildConfig
import com.example.compteur.data.api.LiveTrackingApi
import com.example.compteur.data.api.StravaApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideStravaApi(okHttpClient: OkHttpClient, moshi: Moshi): StravaApi {
        return Retrofit.Builder()
            .baseUrl("https://www.strava.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(StravaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideLiveTrackingApi(okHttpClient: OkHttpClient, moshi: Moshi): LiveTrackingApi {
        // baseUrl n'est qu'un placeholder : l'URL réelle du serveur est un réglage utilisateur,
        // passée à chaque appel via @Url.
        return Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(LiveTrackingApi::class.java)
    }
}
