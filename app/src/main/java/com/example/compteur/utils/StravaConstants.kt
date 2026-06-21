package com.example.compteur.utils

import com.example.compteur.BuildConfig

object StravaConstants {
    // Identifiants lus depuis BuildConfig (alimentés par local.properties, non versionné)
    val CLIENT_ID = BuildConfig.STRAVA_CLIENT_ID
    val CLIENT_SECRET = BuildConfig.STRAVA_CLIENT_SECRET

    const val AUTH_URL = "https://www.strava.com/oauth/mobile/authorize"
    // Schéma custom intercepté par MainActivity (voir intent-filter dans AndroidManifest).
    // Côté dashboard Strava, "Authorization Callback Domain" doit être réglé sur "strava".
    const val REDIRECT_URI = "compteur://strava"
    const val SCOPE = "activity:write,read"
}
